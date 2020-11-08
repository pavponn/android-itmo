package com.example.animation.drawable

import com.example.animation.R
import kotlin.math.sign

class DrawableLoaders {

    companion object {
        fun squares(size: Int, colRowsCount: Int, speed: Float): List<DrawableSquare> {
            val squares = ArrayList<DrawableSquare>()

            for (i in -(colRowsCount / 2)..(colRowsCount / 2)) {
                for (j in -(colRowsCount / 2)..(colRowsCount / 2)) {
                    val square = DrawableSquare(
                        R.drawable.ic_rubik,
                        size,
                        speed,
                        2 * size * i,
                        2 * size * j,
                        (sign((i + j).toDouble()).toFloat() * (i + j).toFloat() + 1) * 0.65F
                    )
                    squares.add(square)
                }
            }

            return squares
        }


        fun circles(radius: Int, offset: Int, speed: Float, quantity: Int): List<DrawableCircle> {
            val circles = ArrayList<DrawableCircle>()
            for (i in 0 until quantity) {
                val circle = DrawableCircle(
                    R.drawable.ic_loading,
                    i * 360F / quantity,
                    radius,
                    speed,
                    offset
                )
                circles.add(circle)
            }
            return circles
        }
    }
}