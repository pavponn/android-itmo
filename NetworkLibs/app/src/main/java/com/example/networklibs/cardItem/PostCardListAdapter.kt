package com.example.networklibs.cardItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.networklibs.R


class PostCardListAdapter(
    private val posts: MutableList<Post>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<PostCardListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCardListViewHolder {
        return PostCardListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.post_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostCardListViewHolder, position: Int) {
        holder.titleText.text = posts[position].title
        holder.bodyText.text = posts[position].body
        holder.deleteButton.setOnClickListener { onClick(position) }
    }

    companion object {
        private const val TAG = "PostCardListAdapter"
    }


}