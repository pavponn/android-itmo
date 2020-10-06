package com.example.networking.cardItem

import android.graphics.Bitmap

data class ImageCard (
    var preview: Bitmap? = null,
    var description: String = "",
    var previewUrl: String = "",
    var highResUrl: String = ""
)