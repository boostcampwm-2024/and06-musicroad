package com.squirtles.feature.map.marker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

@SuppressLint("ViewConstructor")
class ClusterMarkerIconView(
    context: Context,
    private val densityType: DensityType
) : View(context) {

    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(MARKER_WIDTH.dpToPx(), widthMeasureSpec)
        val height = resolveSize(MARKER_HEIGHT.dpToPx(), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        fillPaint.color = densityType.color

        // back circle
        fillPaint.alpha = 64
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            (width / 2f) - densityType.offset,
            fillPaint
        )

        // circle
        fillPaint.alpha = 255
        canvas.drawCircle(
            width / 2f,
            height / 2f,
            (width / 2f) - OFFSET - densityType.offset,
            fillPaint
        )
    }

    private fun Int.dpToPx() = (this * resources.displayMetrics.density).toInt()

    companion object {
        private const val OFFSET = 8
        private const val MARKER_WIDTH = 40
        private const val MARKER_HEIGHT = 50
    }
}
