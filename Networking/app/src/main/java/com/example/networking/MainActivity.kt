package com.example.networking

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networking.cardItem.ImageCard
import com.example.networking.cardItem.ImageCardListAdapter
import com.example.networking.config.*
import com.example.networking.url.UrlCreator
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var vkConfig: VkConfig
    private var asyncTask: ImageLoader? = null
    private lateinit var mViewModel: MyViewModel

    companion object {
        private const val DEFAULT_QUERY = "коронавирус"
    }

    class MyViewModel : ViewModel() {
        var queryList: List<ImageCard>? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        initConfig()

        setSearchViewListeners()

        main_progress_bar.visibility = View.GONE
        if (mViewModel.queryList == null) {
            executeQuery(DEFAULT_QUERY)
        }
        setImageCardList(mViewModel.queryList)

        asyncTask = (lastCustomNonConfigurationInstance as? ImageLoader) ?: ImageLoader(this)
        asyncTask?.attachActivity(this)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any? = asyncTask

    override fun onDestroy() {
        asyncTask?.activityRef = WeakReference(null)
        super.onDestroy()
    }

    private fun setSearchViewListeners() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                executeQuery(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun initConfig() {
        val stream: InputStream = this.assets.open("defaults.properties")
        stream.use {
            val props = Properties()
            props.load(stream)
            vkConfig = VkConfig(
                props.getProperty(vkServiceKey),
                UrlConfig(
                    props.getProperty(vkUrlSchema),
                    props.getProperty(vkUrlHost)
                ),
                Version(
                    props.getProperty(vkVersionMajor).toInt(),
                    props.getProperty(vkVersionMinor).toInt()
                )
            )
        }
    }


    private fun executeQuery(query: String?) {
        asyncTask?.activityRef = WeakReference(null)
        asyncTask = ImageLoader(this).apply {
            val requestUrl = getQueryUrl(query)
            execute(requestUrl)
            attachActivity(this@MainActivity)
        }
    }

    internal fun onLoadCompleted(result: List<ImageCard>?) {
        if (result != null) {
            setImageCardList(result)
        }

        asyncTask = null
    }

    private fun getQueryUrl(query: String?): String {
        val queryToUse = query ?: ""
        val params = mapOf(
            "access_token" to vkConfig.accessToken,
            "q" to queryToUse,
            "count" to "50",
            "sort" to "0",
            "v" to "${vkConfig.version.major}.${vkConfig.version.minor}"
        )
        val baseUrl = "${vkConfig.urlConfig.schema}://${vkConfig.urlConfig.host}/method/photos.search"
        return UrlCreator().getUrlString(baseUrl, params)
    }


    private fun setImageCardList(imageCardList: List<ImageCard>?) {
        mViewModel.queryList = imageCardList
        if (imageCardList != null) {
            main_recycler_view.visibility = View.VISIBLE
            main_recycler_view.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = ImageCardListAdapter(imageCardList) {
                    startActivity(Intent(this@MainActivity, ImageActivity::class.java).apply {
                        putExtra(HIGH_RES_URL_TOKEN, it.highResUrl)
                        putExtra(POST_TEXT_TOKEN, it.description)
                    })
                }
            }
        }
    }

    private class ImageLoader(activity: MainActivity?) : AsyncTask<String, Int, List<ImageCard>>() {
        var activityRef = WeakReference(activity)
        private var cachedResult: List<ImageCard>? = null

        override fun onPreExecute() {
            super.onPreExecute()
            activityRef.get()?.apply {
                main_recycler_view.visibility = View.GONE
                main_progress_bar.visibility = View.VISIBLE
                main_progress_bar.progress = 0
                main_progress_bar.max = 50
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            activityRef.get()?.main_progress_bar?.apply {
                visibility = View.VISIBLE
                progress = values[0]!!
            }
        }

        override fun doInBackground(vararg params: String?): List<ImageCard> {
            return getListFromResponse(
                URL(params[0]).openConnection().getInputStream().reader().readText()
            )
        }

        override fun onPostExecute(result: List<ImageCard>?) {
            super.onPostExecute(result)
            activityRef.get()?.main_progress_bar!!.visibility = View.GONE
            activityRef.get()?.main_recycler_view!!.visibility = View.VISIBLE
            activityRef.get()?.onLoadCompleted(result) ?: run {
                cachedResult = result
            }
        }

        fun attachActivity(activity: MainActivity) {
            activityRef = WeakReference(activity)
            cachedResult?.let {
                activity.onLoadCompleted(it)
                cachedResult = null
            }
        }

        private fun downloadPreview(previewUrl: String): Bitmap {
            return BitmapFactory.decodeStream(URL(previewUrl).openConnection().getInputStream())
        }


        private fun getListFromResponse(response: String?): List<ImageCard> {
            if (response == null) {
                return emptyList()
            }
            val json = Gson().fromJson(response, JsonObject::class.java)
            if (!json.has("response")) {
                return emptyList()
            }
            val jsonResponse = json.get("response").asJsonObject
            if (!jsonResponse.has("items")) {
                return emptyList()
            }

            val items = jsonResponse.get("items").asJsonArray
            val count = items.size()
            val responseResult: ArrayList<ImageCard> = ArrayList()
            for (i in 0 until count) {
                val item = items[i].asJsonObject
                responseResult.add(ImageCard().apply {
                    val imageSizes = item.get("sizes").asJsonArray
                    if (item.has("text")) {
                        description = item.get("text").asString
                    }
                    previewUrl = imageSizes[0].asJsonObject.get("url").asString
                    highResUrl = imageSizes.last().asJsonObject.get("url").asString
                    preview = downloadPreview(previewUrl)
                })
                publishProgress(i)
            }
            return responseResult
        }
    }
}