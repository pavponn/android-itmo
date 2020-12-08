package com.example.networklibs.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import retrofit2.Call
import retrofit2.Callback

fun <T> Call<T>.enqueue(lifecycleOwner: LifecycleOwner, callback: Callback<T>) {
    // add an observer to activity which will cancel an ongoing call
    // when activity is destroyed
    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun cancelCalls() {
            this@enqueue.cancel()
        }
    })
    this.enqueue(callback)
}