package com.squirtles.musicroad.detail.components.music.visualizer.soundeffect

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundBarConstants.STROKE_WIDTH
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundEffectConstants.GRADIENT_RADIUS_RATIO
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundEffectConstants.OFFSET_ANGLE
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundEffectConstants.animatedGradientRadius
import com.squirtles.musicroad.ui.theme.White
import kotlin.math.max

/* Bar 형태 원형 시각화 */
@Composable
internal fun DrawSoundBar(
    audioData: List<Float>,
    color: Color,
    useGradient: Boolean = true,
    radiusRatio: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var radius by remember { mutableFloatStateOf(0f) }
    var maxBarHeight by remember { mutableFloatStateOf(0f) }

    val animatedGradientRadius = animatedGradientRadius(LinearEasing)
    val gradientColors = remember {
        listOf(
            color.mixedWhite(),
            color
        )
    }

    val angleStep = 360f / audioData.size

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

        audioData.forEachIndexed { i, magnitude ->
            val angle = Math.toRadians((i * angleStep).toDouble()) + OFFSET_ANGLE
            val barHeight = maxBarHeight * magnitude

            val startOffset = getOffset(
                centerX = width / 2,
                centerY = height / 2,
                angle = angle,
                radius = radius,
                extraLength = 0f
            )

            val endOffset = getOffset(
                centerX = width / 2,
                centerY = height / 2,
                angle = angle,
                radius = radius,
                extraLength = barHeight
            )
            
            // Bar별로 그라데이션 적용
//            val barLength = endOffset.minus(startOffset).getDistance()
//            val sharedRadius = (barLength * animatedGradientRadius).coerceAtLeast(0.1f)

            if (useGradient) {
                drawLine(
                    brush = Brush.radialGradient(
                        colors = gradientColors,
                        center = startOffset,
                        radius = animatedGradientRadius * maxBarHeight * GRADIENT_RADIUS_RATIO,
                    ),
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = STROKE_WIDTH
                )
            } else {
                drawLine(
                    color = color,
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = STROKE_WIDTH
                )
            }
        }
    }
}

private fun Color.mixedWhite(): Color {
    val gradientColor = White
    return Color(
        red = ((this.red + gradientColor.red) / 2),
        green = ((this.green + gradientColor.green) / 2),
        blue = ((this.blue + gradientColor.blue) / 2),
    )
}
