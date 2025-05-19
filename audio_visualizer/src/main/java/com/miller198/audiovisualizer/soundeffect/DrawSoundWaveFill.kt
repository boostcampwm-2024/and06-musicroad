package com.miller198.audiovisualizer.soundeffect

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs.animatedGradientRadius
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs.gradientConfig
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs.onCanvasSizeChanged

/**
 * Composable function that draws a smooth radial waveform (filled area).
 *
 * This effect visualizes the waveform in a circular path using Catmull-Rom splines.
 *
 * @param audioData The list of normalized audio magnitudes (range: 0.0f to 1.0f).
 * @param color The primary color used to fill the waveform.
 * @param modifier Modifier to apply to the Canvas layout.
 */
@Composable
internal fun DrawSoundWaveFill(
    audioData: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    /** The radius from the center to the start of the bars */
    var adjustedRadius by remember { mutableFloatStateOf(0f) }

    /**  The maximum possible bar height based on canvas size */
    var maxEffectHeight by remember { mutableFloatStateOf(0f) }

    /** Path used to define the circular center hole (to clip out) */
    var holePath by remember { mutableStateOf(Path()) }

    /** Angle between each bar in the 360° circle */
    val angleStep = 360f / audioData.size

    /** The path that represents the full waveform */
    val path = Path()

    /** Animated gradient radius for visual effect */
    val animatedGradientRadius = animatedGradientRadius(LinearOutSlowInEasing)

    /** Main canvas for drawing the sound wave */
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize ->
                // Recalculate radius and effect height when the canvas size changes
                onCanvasSizeChanged(
                    width = canvasSize.width,
                    height = canvasSize.height,
                    onRadiusCalculated = { adjustedRadius = it },
                    onMaxEffectHeightCalculated = { maxEffectHeight = it }
                )
                holePath = holePath.apply {
                    addOval(
                        Rect(
                            center = Offset(x = canvasSize.width / 2f, y = canvasSize.height / 2f),
                            radius = adjustedRadius + 1f
                        )
                    )
                }
            }
    ) {
        val width = size.width
        val height = size.height

        // Calculate wave points positioned radially
        val points = audioData.mapIndexed { idx, magnitude ->
            val angle = getAudioDataAngle(idx, angleStep)
            val waveHeight = maxEffectHeight * magnitude
            getOffset(
                centerX = width / 2,
                centerY = height / 2,
                angle = angle,
                innerRadius = adjustedRadius,
                extraLength = waveHeight,
            )
        }

        // Apply Catmull-Rom spline for smooth curve interpolation
        path.catmullRomSpline(points)

        // Clip the central hole area and draw the wave around it
        clipPath(holePath, clipOp = ClipOp.Difference) {
            if (gradientConfig.useGradient) {
                drawPath(
                    path = path,
                    brush = Brush.radialGradient(
                        colors = listOf(gradientConfig.color, color),
                        center = Offset(width / 2, height / 2),
                        radius = (adjustedRadius + maxEffectHeight).coerceAtLeast(0.01f)
                                * animatedGradientRadius
                    )
                )
            } else {
                drawPath(
                    path = path,
                    color = color
                )
            }
        }
    }
}
