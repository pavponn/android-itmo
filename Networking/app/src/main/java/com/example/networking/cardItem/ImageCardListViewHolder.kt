package com.example.networking.cardItem

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.image_item.view.*

class ImageCardListViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val imagePreview: ImageView = root.card_image_preview_image
    val imageDescription: TextView = root.card_image_preview_text
}