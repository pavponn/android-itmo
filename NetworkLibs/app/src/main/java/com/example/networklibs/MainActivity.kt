package com.example.networklibs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networklibs.MainApplication.Companion.mainApplication
import com.example.networklibs.cardItem.Post
import com.example.networklibs.cardItem.PostCardListAdapter
import com.example.networklibs.extensions.enqueue
import com.example.networklibs.random.RandomStringGenerator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
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
        val call = mainApplication.service.getPosts()
        showProgressBarAndHideRecycler()
        call.enqueue(this@MainActivity, object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                val message = resources.getString(R.string.load_error, t.message)
                Log.e(TAG, message)
                showSnackbar(message)
                hideProgressBarAndShowRecycler()
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val message = getLoadMessageByResponse(response)
                Log.i(TAG, message)
                hideProgressBarAndShowRecycler()
                setPostsList(response.body()?.toMutableList())
            }
        })
    }

    private fun addPost() {
        val call = mainApplication.service.addPost(generateRandomRequestBody())

        call.enqueue(this@MainActivity, object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                val message = resources.getString(R.string.add_error, t.message)
                Log.e(TAG, message)
                showSnackbar(message)
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                val message = getAddMessageByResponse(response)
                Log.i(TAG, message)
                showSnackbar(message)
                addPostToModelView(response.body())
            }
        })
    }

    private fun deletePost(position: Int) {
        if (mViewModel.postsList == null) {
            return
        }
        val postId = mViewModel.postsList!![position].id
        val call = mainApplication.service.deletePost(postId)

        call.enqueue(this@MainActivity, object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val message = resources.getString(R.string.delete_error, t.message)
                Log.e(TAG, message)
                showSnackbar(message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val message = getDeleteMessageByResponse(response)
                Log.i(TAG, message)
                showSnackbar(message)
                deletePostFromModelView(position)
            }
        })
    }

    private fun setPostsList(posts: MutableList<Post>?) {
        mViewModel.postsList = posts
        if (posts != null) {
            main_recycler_view.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = PostCardListAdapter(posts) { deletePost(it) }
            }
        }
    }

    private fun generateRandomRequestBody(): Map<String, String> {
        val titleLength = Random.nextInt(8, 20)
        val bodyLength = Random.nextInt(50, 200)
        val title = RandomStringGenerator.getRandomString(titleLength)
        val body = RandomStringGenerator.getRandomString(bodyLength)
        val userId = Random.nextInt(1, 1000)
        return mapOf("title" to title, "body" to body, "userId" to userId.toString())
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

    private fun addPostToModelView(post: Post?) {
        if (post == null) {
            return
        }
        mViewModel.postsList?.add(post)
        main_recycler_view.recycledViewPool.clear()
        main_recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun deletePostFromModelView(position: Int) {
        if (mViewModel.postsList == null || position >= mViewModel.postsList!!.size) {
            return
        }

        mViewModel.postsList!!.removeAt(position)
        main_recycler_view.recycledViewPool.clear()
        main_recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun showSnackbar(message: String) {
        Snackbar
            .make(main_layout, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.action_ok) {}
            .show()
    }

    private fun <T> getLoadMessageByResponse(response: Response<T>): String {
        return getMessageByResponse(response, R.string.load_success, R.string.load_fail)
    }

    private fun <T> getAddMessageByResponse(response: Response<T>): String {
        return getMessageByResponse(response, R.string.add_success, R.string.add_fail)
    }

    private fun <T> getDeleteMessageByResponse(response: Response<T>): String {
        return getMessageByResponse(response, R.string.delete_success, R.string.delete_fail)
    }

    private fun <T> getMessageByResponse(
        response: Response<T>,
        successMsgId: Int,
        failMsgId: Int
    ): String {
        return if (response.isSuccessful) {
            mainApplication.appResources
                .getString(successMsgId, "${response.code()}")
        } else {
            mainApplication.appResources
                .getString(failMsgId, "${response.code()}")
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}