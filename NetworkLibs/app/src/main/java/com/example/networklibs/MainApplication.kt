package com.example.networklibs

import android.app.Application
import com.example.networklibs.service.JsonPlaceholderService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainApplication : Application() {
    lateinit var retrofit: Retrofit
    lateinit var service: JsonPlaceholderService

    override fun onCreate() {
        super.onCreate()
        instance = this
        retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        service = retrofit.create(JsonPlaceholderService::class.java)

    }

    companion object {
        lateinit var instance: MainApplication
            private set
    }
}