package com.example.animation.drawable

import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable


class DrawableSquare(
    override val imageId: Int,
    private val size: Int,
    private val speed: Float,
    private val xOffset: Int,
    private val yOffset: Int,
    private var multiplier: Float
) : AbstractDrawableElement(imageId) {
    private var increase = 1

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat()
    ) {
        increase = parcel.readInt()
    }

    override fun drawImpl(canvas: Canvas, toDraw: Boolean) {
        val xCenter = view!!.pivotX.toInt()
        val yCenter = view!!.pivotY.toInt()
        image.setBounds(
            (xCenter + xOffset - multiplier * size / 2).toInt(),
            (yCenter + yOffset - multiplier * size / 2).toInt(),
            (xCenter + xOffset + multiplier * size / 2).toInt(),
            (yCenter + yOffset + multiplier * size / 2).toInt()
        )
        if (multiplier >= MAX_MULTIPLIER) {
            increase = 0
        }
        if (multiplier <= MIN_MULTIPLIER) {
            increase = 1
        }

        if (toDraw) {
            if (increase == 1) {
                multiplier *= speed
            } else {
                multiplier /= speed
            }
        }
        image.draw(canvas)

    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(imageId)
        dest?.writeInt(size)
        dest?.writeFloat(speed)
        dest?.writeInt(xOffset)
        dest?.writeInt(yOffset)
        dest?.writeFloat(multiplier)
        dest?.writeInt(increase)
    }

    companion object CREATOR : Parcelable.Creator<DrawableSquare> {
        override fun createFromParcel(parcel: Parcel): DrawableSquare {
            return DrawableSquare(parcel)
        }

        override fun newArray(size: Int): Array<DrawableSquare?> {
            return arrayOfNulls(size)
        }

        private const val MAX_MULTIPLIER = 1.95F
        private const val MIN_MULTIPLIER = 0.95F
    }

}