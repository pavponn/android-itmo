package com.example.animation.drawable

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources

abstract class AbstractDrawableElement(protected open val imageId: Int) : Parcelable {

    protected lateinit var image: Drawable

    var view: View? = null
        set(v) {
            field = v
            if (v != null) {
                image = AppCompatResources.getDrawable(v.context, imageId)!!
            }

        }

    fun draw(canvas: Canvas, toDraw: Boolean = false) {
        drawImpl(canvas, toDraw)
        view!!.invalidate()
    }

    protected abstract fun drawImpl(canvas: Canvas, toDraw: Boolean = false)

    override fun describeContents(): Int {
        return 0
    }

}