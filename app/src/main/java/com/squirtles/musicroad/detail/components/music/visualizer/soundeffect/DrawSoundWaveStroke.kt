package com.squirtles.musicroad.detail.components.music.visualizer.soundeffect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundEffectConstants.OFFSET_ANGLE
import kotlin.math.max

/* Wave 형태 원형 시각화 */
@Composable
internal fun DrawSoundWaveStroke(
    audioData: List<Float>,
    color: Color,
    radiusRatio: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var radius by remember { mutableFloatStateOf(0f) }
    var maxBarHeight by remember { mutableFloatStateOf(0f) }

    val angleStep = 360f / audioData.size
    val path = Path()

    LaunchedEffect(canvasSize) {
        radius = max(canvasSize.width, canvasSize.height) * radiusRatio
        maxBarHeight = (canvasSize.height / 4).toFloat()
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged {
                canvasSize = it
            }
    ) {
        val width = size.width
        val height = size.height

        val points = audioData.mapIndexed { i, magnitude ->
            val angle = Math.toRadians((i * angleStep).toDouble()) + OFFSET_ANGLE
            val barHeight = maxBarHeight * magnitude
            getOffset(
                centerX = width / 2,
                centerY = height / 2,
                angle = angle,
                radius = radius,
                extraLength = barHeight,
            )
        }

        path.catmullRomSpline(points)

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = DrawSoundWaveStrokeConstants.STROKE_WIDTH)
        )
    }
}
