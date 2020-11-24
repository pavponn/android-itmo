package com.example.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.persistence.cardItem.Post

@Dao
interface PostsDao {
    @Query("select * from Posts")
    suspend fun getAll(): List<Post>

    @Insert
    suspend fun insert(vararg post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Query("delete from Posts where id = :id")
    suspend fun deleteById(id: Int)

    @Query("delete from Posts")
    suspend fun deleteAll()
}