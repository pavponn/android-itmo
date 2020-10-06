package com.example.networking.cardItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.networking.R
import kotlinx.android.synthetic.main.image_item.*

class ImageCardListAdapter(
    private val imageCards: List<ImageCard>,
    val onClick: (ImageCard) -> Unit
): RecyclerView.Adapter<ImageCardListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardListViewHolder {
        return ImageCardListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.image_item,
                parent,
                false
            )
        ).apply {
            root.setOnClickListener {
                onClick(imageCards[adapterPosition]);
            }
        }
    }

    override fun getItemCount(): Int {
        return imageCards.size
    }

    override fun onBindViewHolder(holder: ImageCardListViewHolder, position: Int) {
        holder.imagePreview.setImageBitmap(imageCards[position].preview)
        holder.imageDescription.text = imageCards[position].description
    }
}