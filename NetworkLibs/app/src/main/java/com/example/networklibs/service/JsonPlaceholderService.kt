package com.example.networklibs.service

import com.example.networklibs.cardItem.Post
import retrofit2.Call
import retrofit2.http.*


interface JsonPlaceholderService {

    @GET("/posts")
    fun getPosts(): Call<List<Post>>

    @POST("/posts")
    fun addPost(
        @Query("title") title: String,
        @Query("body") body: String,
        @Query("userId") userId: Int
    ): Call<Post>

    @DELETE("/posts/{postId}")
    fun deletePost(@Path("postId") postId: Int): Call<Post>

}