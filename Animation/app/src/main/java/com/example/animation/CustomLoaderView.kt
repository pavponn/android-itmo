package com.example.animation

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Parcel
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
        allElements.forEach { it.view = null }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        borderCircles.forEach {
            it.apply {
                xCenter = this@CustomLoaderView.right / 2
                yCenter = this@CustomLoaderView.bottom / 2
                xInitPos = xCenter + offset
                yInitPos = yCenter
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        this.borderCircles = savedState.borderCircles
        this.centerSquares = savedState.centerSquares
        this.isInfiniteAnimation = savedState.isInfiniteAnimation
        this.animationDuration = savedState.animationDuration
        this.startAnimationTime = savedState.startAnimationTime
    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(
            super.onSaveInstanceState(),
            isInfiniteAnimation,
            animationDuration,
            startAnimationTime,
            borderCircles as MutableList<DrawableCircle>,
            centerSquares as MutableList<DrawableSquare>
        )
    }

    class SavedState : BaseSavedState {
        var isInfiniteAnimation: Boolean
            private set
        var startAnimationTime: Long
            private set
        var animationDuration: Long
            private set
        var borderCircles: MutableList<DrawableCircle>
            private set
        var centerSquares: MutableList<DrawableSquare>
            private set

        constructor(
            state: Parcelable?,
            isInfiniteAnimation: Boolean,
            startAnimationTime: Long,
            animationDuration: Long,
            borderCircles: MutableList<DrawableCircle>,
            centerSquares: MutableList<DrawableSquare>
        ) : super(state) {
            this.isInfiniteAnimation = isInfiniteAnimation
            this.startAnimationTime = startAnimationTime
            this.animationDuration = animationDuration
            this.borderCircles = borderCircles
            this.centerSquares = centerSquares
        }

        constructor(state: Parcel?) : super(state) {
            val circlesSize = state!!.readInt()
            this.borderCircles = mutableListOf()
            for (i in 0 until circlesSize) {
                this.borderCircles.add(state.readParcelable(DrawableCircle.javaClass.classLoader)!!)
            }
            val squaresSize = state.readInt()
            centerSquares = mutableListOf()
            for (i in 0 until squaresSize) {
                centerSquares.add(state.readParcelable(DrawableSquare.javaClass.classLoader)!!)
            }
            isInfiniteAnimation = state.readInt() == 1
            animationDuration = state.readLong()
            startAnimationTime = state.readLong()
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            if (out == null) {
                return
            }
            super.writeToParcel(out, flags)
            out.writeInt(borderCircles.size)
            borderCircles.forEach { out.writeParcelable(it, 0) }
            out.writeInt(centerSquares.size)
            centerSquares.forEach { out.writeParcelable(it, 0) }
            out.writeInt(if (isInfiniteAnimation) 1 else 0)
            out.writeLong(animationDuration)
            out.writeLong(startAnimationTime)
        }

        companion object CREATOR : Parcelable.Creator<SavedState?> {
            override fun createFromParcel(source: Parcel?): SavedState? {
                return SavedState(source)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return Array(size) { null }
            }
        }
    }


}