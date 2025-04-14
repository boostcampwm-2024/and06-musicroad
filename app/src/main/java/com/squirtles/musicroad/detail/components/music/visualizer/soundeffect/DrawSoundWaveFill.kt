package com.squirtles.musicroad.detail.components.music.visualizer.soundeffect

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundEffectConfigs.OFFSET_ANGLE
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundEffectConfigs.animatedGradientRadius
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundEffectConfigs.onCanvasSizeChanged
import com.squirtles.musicroad.ui.theme.White

/* Wave 형태 원형 시각화 */
@Composable
internal fun DrawSoundWaveFill(
    audioData: List<Float>,
    color: Color,
    useGradient: Boolean = true,
    radius: Dp = 0.dp,
    radiusRatio: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var adjustedRadius by remember { mutableFloatStateOf(0f) }
    var maxBarHeight by remember { mutableFloatStateOf(0f) }
    var holePath by remember { mutableStateOf(Path()) }
    var center by remember { mutableStateOf(Offset(0f, 0f)) }

    val angleStep = 360f / audioData.size
    val path = Path()

    val animatedGradientRadius = animatedGradientRadius(FastOutSlowInEasing)

    LaunchedEffect(canvasSize) {
        onCanvasSizeChanged(
            width = canvasSize.width,
            height = canvasSize.height,
            radius = radius.value,
            radiusRatio = radiusRatio,
            onRadiusCalculated = { adjustedRadius = it },
            onMaxBarHeightCalculated = { maxBarHeight = it }
        )
        center = Offset(x = canvasSize.width / 2f, y = canvasSize.height / 2f)
        holePath = holePath.apply {
            addOval(
                Rect(
                    center = Offset(x = canvasSize.width / 2f, y = canvasSize.height / 2f),
                    radius = adjustedRadius + 1f
                )
            )
        }
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
                radius = adjustedRadius,
                extraLength = barHeight,
            )
        }

        val wavePath = path.apply {
            catmullRomSpline(points)
            close()
        }

        // 중앙 영역 제외한 나머지 부분만 그리기
        clipPath(holePath, clipOp = ClipOp.Difference) {
            if (useGradient) {
                drawPath(
                    path = wavePath,
                    brush = Brush.radialGradient(
                        colors = listOf(White, color),
                        center = Offset(width / 2, height / 2),
                        radius = (adjustedRadius + maxBarHeight).coerceAtLeast(0.01f)
                                * animatedGradientRadius
                    )
                )
            } else {
                drawPath(
                    path = wavePath,
                    color = color
                )
            }
        }
    }
}
