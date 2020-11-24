package com.example.persistence

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.persistence.MainApplication.Companion.appInstance
import com.example.persistence.cardItem.Post
import com.example.persistence.cardItem.PostCardListAdapter
import com.example.persistence.random.RandomStringGenerator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
            setViewPostsList(mViewModel.postsList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reset_entry, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                refreshView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadAndSetPostsList() {
        showProgressBarAndHideRecycler()
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val defaultValue = resources.getBoolean(R.bool.default_load_from_db)
        val loadFromDbFlag =
            sharedPref.getBoolean(getString(R.string.saved_load_from_db_flag), defaultValue)

        if (loadFromDbFlag) {
            Log.d(TAG, "Fetching from db")
            lifecycleScope.launch {
                val postsFromDb = appInstance.db.postsDao().getAll()
                setViewPostsList(postsFromDb.toMutableList())
            }
            hideProgressBarAndShowRecycler()
            return
        }

        Log.d(TAG, "Fetching from service")
        val call = appInstance.service.getPosts()
        call.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                val message = resources.getString(R.string.load_error, t.message)
                Log.e(TAG, message)
                showSnackbar(message)
                hideProgressBarAndShowRecycler()
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val message =
                    resources.getString(R.string.load_response, "status code ${response.code()}")
                Log.i(TAG, message)
                val posts: List<Post> = response.body() ?: emptyList()
                addPostsToDbCoroutine(*posts.toTypedArray())
                setLoadFromDbSharedPreferences(true)
                setViewPostsList(posts.toMutableList())
                hideProgressBarAndShowRecycler()
            }
        })
    }

    fun deletePost(position: Int) {
        if (mViewModel.postsList == null) {
            return
        }
        val postId = mViewModel.postsList!![position].id
        val call = appInstance.service.deletePost(postId)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val message = appInstance.appResources.getString(R.string.delete_error, t.message)
                Log.e(TAG, message)
                showSnackbar(message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val message = appInstance.appResources
                    .getString(R.string.delete_response, "status code ${response.code()}")
                Log.i(TAG, message)
                showSnackbar(message)
                deletePostByIdFromDbCoroutine(postId)
                deletePostFromModelView(position)

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
                showSnackbar(message)
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                val message =
                    resources.getString(R.string.add_response, "status code ${response.code()}")
                Log.i(TAG, message)
                showSnackbar(message)
                val postToInsert = Post(
                    0,
                    response.body()?.title ?: "title",
                    response.body()?.body ?: "body",
                    response.body()?.userId ?: 1
                )
                addPostsToDbCoroutine(postToInsert)
                addPostToModelView(postToInsert)
            }
        })
    }

    private fun setViewPostsList(posts: MutableList<Post>?) {
        mViewModel.postsList = posts
        if (posts != null) {
            main_recycler_view.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = PostCardListAdapter(posts, this@MainActivity)
            }
        }
    }

    private fun refreshView() {
        deleteAllPostsDbCoroutine()
        setLoadFromDbSharedPreferences(false)
        loadAndSetPostsList()
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

    private fun addPostsToDbCoroutine(vararg posts: Post) {
        GlobalScope.launch {
            appInstance.db.postsDao().insert(*posts)
        }
    }

    private fun deleteAllPostsDbCoroutine() {
        GlobalScope.launch {
            appInstance.db.postsDao().deleteAll()
        }
    }

    private fun deletePostByIdFromDbCoroutine(id: Int) {
        GlobalScope.launch {
            appInstance.db.postsDao().deleteById(id)
        }
    }

    private fun setLoadFromDbSharedPreferences(value: Boolean) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(getString(R.string.saved_load_from_db_flag), value)
            apply()
        }
    }

    private fun addPostToModelView(post: Post) {
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


    companion object {
        private const val TAG = "MainActivity"
    }
}
