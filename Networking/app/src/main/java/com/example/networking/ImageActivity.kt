package com.example.networking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import com.example.networking.services.HighResImageLoaderService
import com.example.networking.services.INTENT_SERVICE_ACTION_TOKEN_URL
import com.example.networking.services.INTENT_SERVICE_ACTION_TOKEN_RESPONSE
import kotlinx.android.synthetic.main.activity_image.*

const val HIGH_RES_URL_TOKEN = "url"
const val POST_TEXT_TOKEN = "text"

class ImageActivity : AppCompatActivity() {
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val highResUrl = intent.getStringExtra(HIGH_RES_URL_TOKEN)
        startService(Intent(this, HighResImageLoaderService::class.java).apply {
            putExtra(HIGH_RES_URL_TOKEN, highResUrl)
        })

        if (!trySetImage(highResUrl)) {
            progress_bar_full.visibility = View.VISIBLE
        }

        broadcastReceiver = object : BroadcastReceiver () {
            override fun onReceive(context: Context?, intent: Intent?) {
                val url = intent?.getStringExtra(INTENT_SERVICE_ACTION_TOKEN_URL)
                trySetImage(url)
            }
        }

        registerReceiver(
            broadcastReceiver,
            IntentFilter(INTENT_SERVICE_ACTION_TOKEN_RESPONSE).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
            }
        )
    }

    private fun trySetImage(imageUrl: String?): Boolean {
        if (imageUrl != null) {
            val highResImage = HighResImageLoaderService.getImageWithUrl(imageUrl)
            if (highResImage != null) {
                setImage(highResImage)
            } else {
                return false
            }
        } else {
            return false
        }
        return true
    }

    private fun setImage(image: Bitmap) {
        image_view_full.visibility = View.VISIBLE
        progress_bar_full.visibility = View.GONE
        image_view_full.setImageBitmap(image)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}