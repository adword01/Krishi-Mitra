package com.example.krishimitra.models

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DotProgressBar(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private val paint = Paint()
    private var radius = 0
    private var circleSpacing = 0
    private var circleCount = 0
    private var progress = 0

    init {
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        radius = dpToPx(8f).toInt()
        circleSpacing = dpToPx(8f).toInt()
        circleCount = 5
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val width = width - paddingLeft - paddingRight
            val height = height - paddingTop - paddingBottom
            val totalSpacing = circleSpacing * (circleCount - 1)
            val totalDiameter = radius * 2 * circleCount + totalSpacing
            var startX = (width - totalDiameter) / 2f + radius
            val centerY = height / 2f

            for (i in 0 until circleCount) {
                paint.alpha = if (i < progress) 255 else 50
                it.drawCircle(startX, centerY, radius.toFloat(), paint)
                startX += radius * 2 + circleSpacing
            }
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }
}
