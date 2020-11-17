package com.example.networklibs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networklibs.MainApplication.Companion.instance
import com.example.networklibs.cardItem.Post
import com.example.networklibs.cardItem.PostCardListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var mViewModel: MyViewModel

    class MyViewModel : ViewModel() {
        var queryList: List<Post>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        main_progress_bar.visibility = View.GONE
        if (mViewModel.queryList == null) {
            loadAndSetPostsList()
        } else {
            setPostsList(mViewModel.queryList)
        }
    }

    private fun loadAndSetPostsList() {
        val call = instance.service.getPosts()
        showProgressBarAndHideRecycler()

        call.enqueue(object : Callback<List<Post>> {
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e(TAG, "ERROR!")
            }

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                hideProgressBarAndShowRecycler()
                mViewModel.queryList = response.body()
                setPostsList(mViewModel.queryList)
                Log.i(TAG, "LOADED!")
            }
        })
    }

    private fun setPostsList(posts: List<Post>?) {
        mViewModel.queryList = posts
        if (posts != null) {
            main_recycler_view.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = PostCardListAdapter(posts)
            }
        }
    }

    private fun showProgressBarAndHideRecycler() {
        main_progress_bar.visibility = View.VISIBLE
        main_recycler_view.visibility = View.GONE
    }

    private fun hideProgressBarAndShowRecycler() {
        main_progress_bar.visibility = View.GONE
        main_recycler_view.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}