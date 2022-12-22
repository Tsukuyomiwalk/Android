package Telegram.myapplication

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var recyclerViewer: RecyclerView
    lateinit var scrollButton: Button
    var service: MyService? = null
    var bounded = false

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            val connectivityBinder = binder as MyService.MyBinder
            service = connectivityBinder.myService()
            bounded = true
            recyclerViewer = findViewById(R.id.messages)
            recyclerViewer.apply {
                adapter = MessageAdapter(this@MainActivity, service!!.mess)
                layoutManager = LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL, false)
            }

            recyclerViewer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    recyclerViewer.adapter?.let {
                        if (dy < 0) {
                            scrollButton.visibility = View.VISIBLE
                        } else {
                            scrollButton.visibility = View.INVISIBLE
                        }
                    }
                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bounded = false
            service = null
        }
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getIntExtra("from", -1) == -1) {
                    recyclerViewer.adapter?.notifyItemRangeInserted(
                        recyclerViewer.adapter!!.itemCount,
                        intent.getIntExtra("receive", 0)
                    )
                } else {
                    recyclerViewer.adapter?.notifyItemRangeInserted(
                        intent.getIntExtra("from", 0),
                        intent.getIntExtra("receive", 0)
                    )
                }
            }

        }

    private var launchImageChoose = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            data?.data?.let { selectedPhotoUri ->
                val intent = Intent("Photo")
                intent.putExtra("uri", selectedPhotoUri.toString())
                getInstance(this).sendBroadcast(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Intent(this, MyService::class.java).apply {
            startService(this)
            bindService(this, serviceConnection, BIND_AUTO_CREATE)
        }
        getInstance(this).registerReceiver(
            messageReceiver, IntentFilter("Upd")
        )

        recyclerViewer = findViewById(R.id.messages)
        scrollButton = findViewById(R.id.scrollButton)

        scrollButton.setOnClickListener {
            if (scrollButton.visibility == View.VISIBLE) {
                recyclerViewer.scrollToPosition(recyclerViewer.adapter!!.itemCount - 1)
                scrollButton.visibility = View.INVISIBLE
            }
        }

        findViewById<Button>(R.id.imageButton).setOnClickListener {
            imageChoose()
        }
    }

    private fun imageChoose() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        launchImageChoose.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        getInstance(this).unregisterReceiver(messageReceiver)
        if (bounded) {
            unbindService(serviceConnection)
        }
    }

    fun howToSend(view: View) {
        val tmp = findViewById<TextView>(R.id.messageField)
        val intent = Intent("pushT").apply {
            putExtra("json", JsonMapper.builder().serializationInclusion(JsonInclude.Include.NON_NULL).build()
                .registerModule(KotlinModule.Builder().build()).writeValueAsString(
                    MessageDTO(user = "bach_bach", message = Data(null, Text(text = tmp.text.toString())), )))
            putExtra("text", tmp.text.toString())
        }
        getInstance(this).sendBroadcast(intent)
    }
}