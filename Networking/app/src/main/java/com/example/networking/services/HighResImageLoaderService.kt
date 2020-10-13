package com.example.networking.services

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.collection.LruCache
import com.example.networking.HIGH_RES_URL_TOKEN
import java.net.URL

const val INTENT_SERVICE_ACTION_TOKEN_RESPONSE = "RESPONSE"
const val INTENT_SERVICE_ACTION_TOKEN_URL = "IMAGE_URL"

@Suppress("DEPRECATION")
class HighResImageLoaderService : IntentService("ImageLoader") {

    init {
        setIntentRedelivery(true)
    }

    companion object {
        private val lruCache: LruCache<String, Bitmap> = LruCache(5)

        fun getImageWithUrl(url: String): Bitmap? {
            return lruCache.get(url)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        val url = intent?.getStringExtra(HIGH_RES_URL_TOKEN) ?: return

        if (lruCache.get(url) == null) {
            lruCache.put(url, BitmapFactory.decodeStream(URL(url).openConnection().getInputStream()))
        }

        sendBroadcast(Intent().apply {
            action = INTENT_SERVICE_ACTION_TOKEN_RESPONSE
        })

        sendBroadcast(Intent().apply {
            action = INTENT_SERVICE_ACTION_TOKEN_RESPONSE
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra(INTENT_SERVICE_ACTION_TOKEN_URL, url)
        })
    }
}