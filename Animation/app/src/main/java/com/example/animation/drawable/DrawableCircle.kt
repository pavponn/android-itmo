package com.example.animation.drawable

import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import com.example.animation.GeometryConstants.Companion.G_180
import com.example.animation.GeometryConstants.Companion.G_360

import kotlin.math.cos
import kotlin.math.sin

class DrawableCircle(
    override val imageId: Int,
    private var curAngle: Float,
    private val radius: Int,
    private val rotationSpeed: Float,
    val offset: Int
) : AbstractDrawableElement(imageId) {

    var initPositionX = -1
    var initPositionY = -1
    var centerX = -1
    var centerY = -1
    var curPositionX = -1
    var curPositionY = -1

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt()
    ) {

        initPositionX = parcel.readInt()
        initPositionY = parcel.readInt()
        centerX = parcel.readInt()
        centerY = parcel.readInt()
        curPositionX = parcel.readInt()
        curPositionY = parcel.readInt()
    }

    override fun drawImpl(canvas: Canvas, toDraw: Boolean) {
        val angle = curAngle * Math.PI / G_180
        val rotatedX =
            cos(angle) * (initPositionX - centerX) - sin(angle) * (initPositionY - centerY) + centerX
        val rotatedY =
            sin(angle) * (initPositionX - centerX) + cos(angle) * (initPositionY - centerY) + centerY
        curPositionX = rotatedX.toInt()
        curPositionY = rotatedY.toInt()
        image.setBounds(
            (rotatedX - radius).toInt(),
            (rotatedY - radius).toInt(),
            (rotatedX + radius).toInt(),
            (rotatedY + radius).toInt()
        )
        if (curAngle >= G_360) {
            curAngle = 0F
        }
        if (toDraw) {
            curAngle += rotationSpeed
        }
        image.draw(canvas)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(imageId)
        dest?.writeFloat(curAngle)
        dest?.writeInt(radius)
        dest?.writeFloat(rotationSpeed)
        dest?.writeInt(offset)
        dest?.writeInt(initPositionX)
        dest?.writeInt(initPositionY)
        dest?.writeInt(centerX)
        dest?.writeInt(centerY)
        dest?.writeInt(curPositionX)
        dest?.writeInt(curPositionY)
    }

    companion object CREATOR : Parcelable.Creator<DrawableCircle> {
        override fun createFromParcel(parcel: Parcel): DrawableCircle {
            return DrawableCircle(parcel)
        }

        override fun newArray(size: Int): Array<DrawableCircle?> {
            return arrayOfNulls(size)
        }
    }
}