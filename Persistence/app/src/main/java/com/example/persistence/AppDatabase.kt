package com.example.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.persistence.cardItem.Post
import com.example.persistence.dao.PostsDao

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postsDao(): PostsDao
}