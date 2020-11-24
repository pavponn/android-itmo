package com.example.persistence

import android.app.Application
import android.content.res.Resources
import com.example.persistence.service.JsonPlaceholderService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainApplication : Application() {
    lateinit var service: JsonPlaceholderService
    lateinit var appResources: Resources

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        service = retrofit.create(JsonPlaceholderService::class.java)
        appResources = resources
    }

    companion object {
        lateinit var appInstance: MainApplication
            private set
    }
}