package com.example.networking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import com.example.networking.services.HighResImageLoaderService
import com.example.networking.services.INTENT_SERVICE_ACTION_TOKEN_BYTE_ARRAY
import com.example.networking.services.INTENT_SERVICE_ACTION_TOKEN_RESPONSE
import kotlinx.android.synthetic.main.activity_image.*

const val HIGH_RES_URL_TOKEN = "url"
const val POST_TEXT_TOKEN = "text"

class ImageActivity : AppCompatActivity() {
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        startService(Intent(this, HighResImageLoaderService::class.java).apply {
            putExtra(HIGH_RES_URL_TOKEN, intent.getStringExtra(HIGH_RES_URL_TOKEN))
        })

        progress_bar_full.visibility = View.VISIBLE

        broadcastReceiver = object : BroadcastReceiver () {
            override fun onReceive(context: Context?, intent: Intent?) {
                val byteArray = intent?.getByteArrayExtra(INTENT_SERVICE_ACTION_TOKEN_BYTE_ARRAY)
                if (byteArray != null) {
                    val byteImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    image_view_full.setImageBitmap(byteImage)
                    image_view_full.visibility = View.VISIBLE
                    progress_bar_full.visibility = View.GONE
                }
            }
        }

        registerReceiver(
            broadcastReceiver,
            IntentFilter(INTENT_SERVICE_ACTION_TOKEN_RESPONSE).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}