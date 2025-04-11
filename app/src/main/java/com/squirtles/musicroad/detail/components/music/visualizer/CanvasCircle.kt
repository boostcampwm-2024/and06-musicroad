package com.squirtles.musicroad.detail.components.music.visualizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.max

@Composable
internal fun CanvasCircle(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val radius = max(width, height) * 0.4f

        drawCircle(
            color = color,
            radius = radius,
            style = Stroke(width = 4f)
        )
    }
}
