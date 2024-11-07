package com.squirtles.musicroad.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.squirtles.musicroad.R

class MarkerIconView(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLACK
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val markerWidth = 30
        val markerHeight = 40
        val width = resolveSize((markerWidth * resources.displayMetrics.density).toInt(), widthMeasureSpec)
        val height = resolveSize((markerHeight * resources.displayMetrics.density).toInt(), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d(TAG_LOG, "width: $width, height: $height")

        canvas.drawCircle(width / 2f, width / 2f, width / 2f, paint)
        canvas.drawRect(width / 2f - 4, width / 2f, width / 2f + 4, height.toFloat(), paint)
    }

    companion object {
        private const val TAG_LOG = "MarkerIconView"
    }
}