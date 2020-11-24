package com.example.persistence.service

import com.example.persistence.cardItem.Post
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface JsonPlaceholderService {

    @GET("/posts")
    fun getPosts(): Call<List<Post>>

    @POST("/posts")
    fun addPost(@Body body: Map<String, String>): Call<Post>

    @DELETE("/posts/{postId}")
    fun deletePost(@Path("postId") postId: Int): Call<ResponseBody>

}