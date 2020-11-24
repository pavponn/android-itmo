package com.example.persistence

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.persistence.MainApplication.Companion.appInstance
import com.example.persistence.cardItem.Post
import com.example.persistence.cardItem.PostCardListAdapter
import com.example.persistence.random.RandomStringGenerator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var mViewModel: MyViewModel

    class MyViewModel : ViewModel() {
        var postsList: MutableList<Post>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        add_post_button.setOnClickListener { addPost() }
        main_progress_bar.visibility = View.GONE
        if (mViewModel.postsList == null) {
            loadAndSetPostsList()
        } else {
            setPostsList(mViewModel.postsList)
        }
    }

    private fun loadAndSetPostsList() {
        val call = appInstance.service.getPosts()
        showProgressBarAndHideRecycler()
        call.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                val message = resources.getString(R.string.load_error, t.message)
                Log.e(TAG, message)
                Snackbar
                    .make(main_layout, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_ok) {}
                    .show()
                hideProgressBarAndShowRecycler()
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val message =
                    resources.getString(R.string.load_response, "status code ${response.code()}")
                Log.i(TAG, message)
                hideProgressBarAndShowRecycler()
                setPostsList(response.body()?.toMutableList())
            }
        })
    }

    private fun addPost() {
        val titleLength = Random.nextInt(5, 10)
        val bodyLength = Random.nextInt(50, 200)
        val title = RandomStringGenerator.getRandomString(titleLength)
        val body = RandomStringGenerator.getRandomString(bodyLength)
        val userId = Random.nextInt(1, 1000)
        val requestBody = mapOf("title" to title, "body" to body, "userId" to userId.toString())
        val call = appInstance.service.addPost(requestBody)

        call.enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                val message = resources.getString(R.string.add_error, t.message)
                Log.e(TAG, message)
                Snackbar
                    .make(main_layout, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_ok) {}
                    .show()
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                val message =
                    resources.getString(R.string.add_response, "status code ${response.code()}")
                Log.i(TAG, message)
                Snackbar
                    .make(main_layout, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_ok) {}
                    .show()
                mViewModel.postsList?.add(0, response.body()!!)
                main_recycler_view.adapter?.notifyItemChanged(0)
                main_recycler_view.adapter?.notifyItemRangeChanged(
                    0,
                    mViewModel.postsList?.size ?: 0
                )
            }
        })
    }

    private fun setPostsList(posts: MutableList<Post>?) {
        mViewModel.postsList = posts
        if (posts != null) {
            main_recycler_view.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = PostCardListAdapter(posts, main_layout)
            }
        }
    }

    private fun showProgressBarAndHideRecycler() {
        main_progress_bar.visibility = View.VISIBLE
        main_recycler_view.visibility = View.GONE
        add_post_button.visibility = View.GONE
    }

    private fun hideProgressBarAndShowRecycler() {
        main_progress_bar.visibility = View.GONE
        main_recycler_view.visibility = View.VISIBLE
        add_post_button.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
