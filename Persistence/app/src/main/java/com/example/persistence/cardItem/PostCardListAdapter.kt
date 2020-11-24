package com.example.persistence.cardItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.persistence.MainActivity
import com.example.persistence.MainApplication.Companion.appInstance
import com.example.persistence.R
import kotlinx.coroutines.launch


class PostCardListAdapter(
    private val posts: MutableList<Post>,
    private val activity: MainActivity
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
        holder.deleteButton.setOnClickListener { activity.deletePost(position) }
    }
}