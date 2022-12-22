package Telegram.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory.*
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import java.io.File
import java.io.FileInputStream

class ImageContributor : AppCompatActivity() {
    private var imgId: Int? = null

    private fun getImageFromCache(imageId: Int): Bitmap? {
        val file = File(cacheDir, "$imageId.png")
        return if (file.exists()) {
            decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreen)
        val image = findViewById<ImageView>(R.id.imageFullscreen)
        imgId = intent.getIntExtra("id", 0)
        image.setImageBitmap(
            getImageFromCache(imageId = imgId!!)
        )
    }
}