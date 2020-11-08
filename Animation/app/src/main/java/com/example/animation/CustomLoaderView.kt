package com.example.animation

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.example.animation.drawable.AbstractDrawableElement
import com.example.animation.drawable.DrawableCircle
import com.example.animation.drawable.DrawableSquare

@SuppressLint("CustomViewStyleable")
class CustomLoaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val ANIMATION_STATE = "ANIMATION_STATE"
    }

    var isInfiniteAnimation = false
    private var startAnimationTime = -1L

    var animationDuration = 0L
        set(value) {
            field = value
            startAnimationTime = System.currentTimeMillis()
            invalidate()
        }

    var borderCircles: List<DrawableCircle> = emptyList()
        set(list) {
            field = list
            field.forEach { it.view = this }
        }

    var centerSquares: List<DrawableSquare> = emptyList()
        set(list) {
            field = list
            field.forEach { it.view = this }
        }

    private val allElements: List<AbstractDrawableElement>
        get() {
            val res = ArrayList<AbstractDrawableElement>(borderCircles)
            res.addAll(centerSquares)
            return res
        }

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Loader)
        try {
            animationDuration = a.getInt(R.styleable.Loader_initialDuration, 0).toLong()
        } finally {
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val currentTime = System.currentTimeMillis()
        val toDraw = currentTime - startAnimationTime < animationDuration || isInfiniteAnimation
        allElements.forEach {
            it.draw(canvas!!, toDraw)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        borderCircles.forEach { it.view = null }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        borderCircles.forEach {
            it.apply {
                centerX = this@CustomLoaderView.right / 2
                centerY = this@CustomLoaderView.bottom / 2
                initPositionX = centerX + offset
                initPositionY = centerY
            }
        }
    }

    // TODO
    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(ANIMATION_STATE, super.onSaveInstanceState())

        return super.onSaveInstanceState()
    }


}