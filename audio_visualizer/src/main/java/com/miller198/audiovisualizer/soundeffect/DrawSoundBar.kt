package com.miller198.audiovisualizer.soundeffect

import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs.GRADIENT_RADIUS_RATIO
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs.animatedGradientRadius
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs.gradientConfig
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs.onCanvasSizeChanged

/**
 * Composable function that draws a radial sound bar visualizer effect.
 *
 * Each audio data point is visualized as a bar (line) extending outward from the center
 * in a circular pattern, with height proportional to the audio magnitude.
 *
 * @param audioData The list of normalized audio magnitudes (range: 0.0f to 1.0f).
 * @param color The primary color used to draw the bars.
 * @param modifier Modifier to apply to the Canvas layout.
 */
@Composable
internal fun DrawSoundBar(
    audioData: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    /** The radius from the center to the start of the bars */
    var adjustedRadius by remember { mutableFloatStateOf(0f) }

    /** The maximum possible bar height based on canvas size */
    var maxEffectHeight by remember { mutableFloatStateOf(0f) }

    /** Animated gradient radius for dynamic glow effects */
    val animatedGradientRadius = animatedGradientRadius(LinearEasing)

    /** Angle between each bar in the 360° circle */
    val angleStep = 360f / audioData.size

    /** Main canvas for drawing the sound bars */
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
            }
    ) {
        val width = size.width
        val height = size.height

        // Draw each audio data bar
        audioData.forEachIndexed { idx, magnitude ->
            val angle = getAudioDataAngle(idx, angleStep)
            val barHeight = maxEffectHeight * magnitude

            // Starting point of the bar (on the inner radius)
            val startOffset = getOffset(
                centerX = width / 2,
                centerY = height / 2,
                angle = angle,
                innerRadius = adjustedRadius,
                extraLength = 0f
            )
            // End point of the bar (based on magnitude)
            val endOffset = getOffset(
                centerX = width / 2,
                centerY = height / 2,
                angle = angle,
                innerRadius = adjustedRadius,
                extraLength = barHeight
            )

            // Draw the line with optional radial gradient
            if (gradientConfig.useGradient) {
                drawLine(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            gradientConfig.color,
                            color
                        ),
                        center = startOffset,
                        radius = animatedGradientRadius * maxEffectHeight * GRADIENT_RADIUS_RATIO,
                    ),
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = DrawSoundBarConstants.STROKE_WIDTH
                )
            } else {
                drawLine(
                    color = color,
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = DrawSoundBarConstants.STROKE_WIDTH
                )
            }
        }
    }
}
