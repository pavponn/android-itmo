package com.example.animation.drawable

import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import com.example.animation.GeometryConstants.Companion.DEGREES_180
import com.example.animation.GeometryConstants.Companion.DEGREES_360

import kotlin.math.cos
import kotlin.math.sin

class DrawableCircle(
    override val imageId: Int,
    private var curAngle: Float,
    private val radius: Int,
    private val rotationSpeed: Float,
    val offset: Int
) : AbstractDrawableElement(imageId) {

    var xInitPos = -1
    var yInitPos = -1
    var xCenter = -1
    var yCenter = -1
    private var xCurPos = -1
    private var yCurPos = -1

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt()
    ) {

        xInitPos = parcel.readInt()
        yInitPos = parcel.readInt()
        xCenter = parcel.readInt()
        yCenter = parcel.readInt()
        xCurPos = parcel.readInt()
        yCurPos = parcel.readInt()
    }

    override fun drawImpl(canvas: Canvas, toDraw: Boolean) {
        val angle = curAngle * Math.PI / DEGREES_180
        val rotatedX =
            (xInitPos - xCenter) * cos(angle) - sin(angle) * (yInitPos - yCenter) + xCenter
        val rotatedY =
            (xInitPos - xCenter) * sin(angle) + cos(angle) * (yInitPos - yCenter) + yCenter
        xCurPos = rotatedX.toInt()
        yCurPos = rotatedY.toInt()
        image.setBounds(
            (rotatedX - radius).toInt(),
            (rotatedY - radius).toInt(),
            (rotatedX + radius).toInt(),
            (rotatedY + radius).toInt()
        )
        if (curAngle >= DEGREES_360) {
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
        dest?.writeInt(xInitPos)
        dest?.writeInt(yInitPos)
        dest?.writeInt(xCenter)
        dest?.writeInt(yCenter)
        dest?.writeInt(xCurPos)
        dest?.writeInt(yCurPos)
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