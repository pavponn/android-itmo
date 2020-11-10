package com.example.animation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import com.example.animation.drawable.DrawableLoaders.Companion.circles
import com.example.animation.drawable.DrawableLoaders.Companion.squares
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ANIMATION_DURATION = 1000L
        private const val INFINITE_ANIMATION = true
        private const val SPEED = 1F
        private const val SPEED_SQUARES = 1F + 1.5F * SPEED / 100F
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loader_view.borderCircles = circles(40, 200, SPEED, 8)
        loader_view.centerSquares = squares(40, 3, SPEED_SQUARES)
        loader_view.animationDuration = ANIMATION_DURATION
        loader_view.isInfiniteAnimation = INFINITE_ANIMATION

        loading_text_view.startAnimation(AlphaAnimation(0F, 1F).apply {
            duration = ANIMATION_DURATION
            repeatCount = AlphaAnimation.INFINITE
            repeatMode = AlphaAnimation.REVERSE
            interpolator = LinearInterpolator()
        })
    }

    override fun onStop() {
        super.onStop()
        loading_text_view.animation.cancel()
    }
}