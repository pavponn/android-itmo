package com.example.networklibs.cardItem

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.post_item.view.*

class PostCardListViewHolder(val root: View): RecyclerView.ViewHolder(root) {
    val titleText: TextView = root.post_title_text
    val bodyText: TextView = root.post_body_text
}