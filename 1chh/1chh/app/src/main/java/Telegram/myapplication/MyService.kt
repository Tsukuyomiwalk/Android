package Telegram.myapplication

import Telegram.db.FailedMessagesDB
import Telegram.db.FailedMessagesEntity
import Telegram.db.MessagesDB
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.*
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.localbroadcastmanager.content.LocalBroadcastManager.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.*
import java.lang.Thread.sleep

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*

class MyService : Service() {
    companion object {
        const val HOST = " http://213.189.221.170:8008/"
    }

    private val db by lazy {
        MessagesDB.getDatabase(this).messagesDAO()
    }
    private val failedDB by lazy {
        FailedMessagesDB.getDatabase(this).failedMessagesDAO()
    }
    val mess = mutableListOf<MessageDTO>()
    private val newRequestFun = object : Runnable {
        override fun run() = Thread {
            val http: HttpURLConnection
            (if (this@MyService.mess.isEmpty()) {
                URL(HOST + "1ch?limit=50")
            } else {
                URL(HOST + "1ch?limit=50&lastKnownId=${mess.last().num}")
            }).apply { http = this.openConnection() as HttpURLConnection }
            try {
                http.connect()
                var str: String
                http.inputStream.use { it -> it.bufferedReader().use { str = it.readText() } }
                val tmp = JsonMapper
                    .builder()
                    .serializationInclusion(JsonInclude.Include.NON_NULL)
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, true).configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true).configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true)
                    .build()
                    .registerModule(KotlinModule.Builder().build()).readValue<MutableList<MessageDTO>>(str)
                for (it in tmp) {
                    if (it.message.image?.link != null) {
                        try {
                            it.message.image.image = decodeStream(URL(HOST + "img/${it.message.image.link}").openStream()).also { image ->
                                writeImageToCache(image, it.num!!)
                            }
                        } catch (e: FileNotFoundException) {
                            println("image without signature")
                        }
                    }
                }
                when {
                    tmp.size != 0 -> {
                        val intent = Intent("Upd")
                        intent.putExtra("from", mess.size)
                        mess += tmp
                        tmp.forEach { db.insert(it.toMessageEntity()) }
                        intent.putExtra("receive", mess.size)
                        getInstance(this@MyService).sendBroadcast(intent)
                    }
                }
                http.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            this@MyService.loopHandle.postDelayed(this, 1000)
        }.start()
    }

    private val sendFailedMessages = object : Runnable {
        override fun run() = Thread {
            failedDB.getAll().forEach {
                val intent = Intent("pushT").apply {
                    putExtra("json", JsonMapper.builder().serializationInclusion(JsonInclude.Include.NON_NULL).build()
                        .registerModule(KotlinModule.Builder().build()).writeValueAsString(
                            MessageDTO(user = "bach_bach", message = Data(null, Text(text = it.text.toString())))))
                    putExtra("text", it.text.toString())
                }
                failedDB.delete(it)
                getInstance(this@MyService).sendBroadcast(intent)
            }
            this@MyService.loopHandle.postDelayed(this, 5000)
        }.start()
    }

    private val forMessage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(Contex: Context?, intent: Intent) {
            val jsonchik: String?
            intent.getStringExtra("json")?.replaceFirst("text", "Text").apply { jsonchik = this }
            Thread {
                try {
                    val con: HttpURLConnection
                    (URL(HOST + "1ch").openConnection() as HttpURLConnection).apply { con = this }
                    con.apply {
                        requestMethod = "POST"
                        doInput = true
                    }
                    con.setRequestProperty("Content-Type", "application/json")
                    con.connect()
                    con.outputStream.use {
                        if (jsonchik != null) {
                            it.write(jsonchik.toByteArray())
                        }
                    }
                    println("ABOBA")
                    con.disconnect()
                } catch (e: Exception) {
                    println("ABOBA2")
                    failedDB.insert(
                        FailedMessagesEntity(
                            0,
                            "bach_bach",
                            intent.getStringExtra("text")
                        )
                    )
                }
            }.start()
        }
    }

    private val forImage: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(Contex: Context?, intent: Intent) {
            val uri = Uri.parse(intent.getStringExtra("uri"))
            if (uri == null || uri == Uri.EMPTY) {
                return
            }
            val image = getImageFromStorage(uri) ?: return
            val code = Date().time.toString()
            val file = getTempFile(image, code)
            Thread {
                try {
                    sendImageMessage(file, code)
                } catch (e: Exception) {

                } finally {
                    file.delete()
                }
            }.start()
        }
    }

    private fun getImageFromStorage(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    uri
                )
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getTempFile(image: Bitmap, code: String): File {
        val file = File(this.cacheDir, "$code.png")
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapData = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return file
    }

    private val loopHandle = Handler(Looper.getMainLooper())

    private fun loadMessages() {
        Thread {
            val intent = Intent("Upd")
            val messages = db.getAll()
            messages.forEach {
                mess.add(it.toMessageDTO().apply {
                    if (this.message.image?.link != null) {
                        message.image.image = getImageFromCache(this.num!!)
                    }
                })
            }
            intent.putExtra("receive", mess.size)
            getInstance(this@MyService).sendBroadcast(intent)
            newRequestFun.run()
            sendFailedMessages.run()
        }.start()
    }

    private fun writeImageToCache(image: Bitmap, imageId: Int) {
        val file =
            File(cacheDir, "$imageId.png").also { it.createNewFile() }
        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapData = bos.toByteArray()
        FileOutputStream(file).use {
            with(it) {
                write(bitmapData)
                flush()
            }
        }
    }

    private fun getImageFromCache(imageId: Int): Bitmap? {
        val file = File(cacheDir, "$imageId.png")
        return if (file.exists()) {
            decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    private fun sendImageMessage(file: File, code: String) {
        val url = URL("http://213.189.221.170:8008/1ch")
        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "POST"
            doInput = true
            doOutput = true
            connectTimeout = 2000
        }

        val boundary = "------$code------"
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

        val crlf = "\r\n"
        val json = "{\"from\":\"bach_bach\"}"
        val outputStream = connection.outputStream
        val outputStreamWriter = OutputStreamWriter(outputStream)
        outputStream.use {
            outputStreamWriter.use {
                with(it) {
                    append("--").append(boundary).append(crlf)
                    append("Content-Disposition: form-data; name=\"json\"").append(crlf)
                    append("Content-Type: application/json; charset=utf-8").append(crlf)
                    append(crlf)
                    append(json).append(crlf)
                    flush()
                    appendFile(file, boundary, outputStream)
                    append(crlf)
                    append("--").append(boundary).append("--").append(crlf)
                }
            }
        }
        connection.disconnect()
    }

    private fun OutputStreamWriter.appendFile(
        file: File,
        boundary: String,
        outputStream: OutputStream,
        crlf: String = "\r\n"
    ) {
        val contentType = URLConnection.guessContentTypeFromName(file.name)
        val fis = FileInputStream(file)
        fis.use {
            append("--").append(boundary).append(crlf)
            append("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"")
            append(crlf)
            append("Content-Type: $contentType").append(crlf)
            append("Content-Length: ${file.length()}").append(crlf)
            append("Content-Transfer-Encoding: binary").append(crlf)
            append(crlf)
            flush()

            val buffer = ByteArray(4096)

            var n: Int
            while (fis.read(buffer).also { n = it } != -1) {
                outputStream.write(buffer, 0, n)
            }
            outputStream.flush()
            append(crlf)
            flush()
        }
    }

    override fun onCreate() {
        super.onCreate()
        getInstance(this).registerReceiver(forMessage, IntentFilter("pushT"))
        getInstance(this).registerReceiver(forImage, IntentFilter("Photo"))
        loadMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        with(loopHandle) { removeCallbacks(newRequestFun) }
        with(loopHandle) { removeCallbacks(sendFailedMessages) }
        getInstance(this).unregisterReceiver(forMessage)
        getInstance(this).unregisterReceiver(forImage)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return MyBinder(this)
    }

    inner class MyBinder(private val myService: MyService) : Binder() {
        fun myService() = myService
    }
}