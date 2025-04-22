package com.squirtles.map.marker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.CircleCropTransformation
import com.squirtles.common.ui.Constants.REQUEST_IMAGE_SIZE_DEFAULT
import com.squirtles.model.Pick

class LeafMarkerIconView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }
    private val imageLoader = SingletonImageLoader.get(context)
    private var bitmap: Bitmap? = null
    private val bitmapRect = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(MARKER_WIDTH.dpToPx(), widthMeasureSpec)
        val height = resolveSize(MARKER_HEIGHT.dpToPx(), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(
            (width - STROKE_WIDTH) / 2f,
            width / 2f,
            (width + STROKE_WIDTH) / 2f,
            height.toFloat(),
            fillPaint
        )
        bitmap?.let {
            bitmapRect.set(0, 0, width, width)
            canvas.drawBitmap(it, null, bitmapRect, null)
            canvas.drawCircle(
                width / 2f,
                width / 2f,
                (width - STROKE_WIDTH) / 2f,
                strokePaint
            ) // bitmap 테두리 그리기
        } ?: canvas.drawCircle(
            width / 2f,
            width / 2f,
            width / 2f,
            fillPaint
        ) // bitmap이 null이면 이미지 없이 원만 그려지도록
    }

    fun setPaintColor(@ColorInt color: Int) {
        fillPaint.color = color
        strokePaint.color = color
    }

    fun setLeafMarkerIcon(imgUrl: String?, onImageLoaded: () -> Unit) {
        loadImage(imgUrl) {
            onImageLoaded()
        }
    }

    private fun loadImage(url: String?, onImageLoaded: () -> Unit) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .transformations(CircleCropTransformation())
            .listener(
                onSuccess = { _, result ->
                    bitmap = result.image.toBitmap()
                    onImageLoaded()
                },
                onError = { _, error ->
                    onImageLoaded()
                }
            )
            .build()

        imageLoader.enqueue(request)
    }

    private fun Int.dpToPx() = (this * resources.displayMetrics.density).toInt()

    companion object {
        private const val STROKE_WIDTH = 8f
        private const val MARKER_WIDTH = 40
        private const val MARKER_HEIGHT = 50
    }
}
