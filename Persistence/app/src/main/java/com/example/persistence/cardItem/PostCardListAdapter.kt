package com.example.persistence.cardItem

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.persistence.MainApplication.Companion.appInstance
import com.example.persistence.R
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PostCardListAdapter(
    private val posts: MutableList<Post>,
    private val mainView: View
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
        holder.deleteButton.setOnClickListener {
            val call = appInstance.service.deletePost(posts[position].id)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val message = appInstance.appResources.getString(R.string.delete_error, t.message)
                    Log.e(TAG, message)
                    Snackbar
                        .make(mainView, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.action_ok) {}
                        .show()
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val message = appInstance.appResources
                        .getString(R.string.delete_response, "status code ${response.code()}")
                    Log.i(TAG, message)
                    Snackbar
                        .make(mainView, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.action_ok) {}
                        .show()
                    posts.removeAt(position)
                    notifyItemChanged(position)
                    notifyItemRangeChanged(position, posts.size)
                }
            })
        }
    }

    companion object {
        private const val TAG = "PostCardListAdapter"
    }


}