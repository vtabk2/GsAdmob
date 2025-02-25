package com.core.gsadmob

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class AspectRatioLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var widthToHeight: Float = -1f
        set(value) {
            field = value
            invalidate()
        }

    var heightToWidth: Float = -1f
        set(value) {
            field = value
            invalidate()
        }

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout)
            widthToHeight = a.getFloat(R.styleable.AspectRatioLayout_admob_widthToHeight, widthToHeight)
            heightToWidth = a.getFloat(R.styleable.AspectRatioLayout_admob_heightToWidth, heightToWidth)
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth: Int
        val desiredHeight: Int
        if (widthToHeight != -1f) {
            desiredHeight = MeasureSpec.getSize(heightMeasureSpec)
            desiredWidth = (desiredHeight * widthToHeight).toInt()
        } else if (heightToWidth != -1f) {
            desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
            desiredHeight = (desiredWidth * heightToWidth).toInt()
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val desiredWidthMeasureSpec = MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.EXACTLY)
        val desiredHeightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY)
        super.onMeasure(desiredWidthMeasureSpec, desiredHeightMeasureSpec)
    }
}