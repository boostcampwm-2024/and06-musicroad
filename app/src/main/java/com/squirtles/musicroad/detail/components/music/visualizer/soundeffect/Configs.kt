package com.squirtles.musicroad.detail.components.music.visualizer.soundeffect

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import kotlin.math.min

internal object DrawSoundEffectConfigs {
    val OFFSET_ANGLE = Math.toRadians(-90.0)
    const val GRADIENT_RADIUS_RATIO = 0.8f
    const val GRADIENT_DURATION = 2500
    const val MAX_BAR_HEIGHT_DIVISOR = 5f

    val animatedGradientRadius: @Composable (Easing) -> Float = { easing ->
        val transition = rememberInfiniteTransition(label = "GradientRadiusTransition")
        transition.animateFloat(
            initialValue = 0.01f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = GRADIENT_DURATION, easing = easing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "AnimatedGradientRadius"
        ).value
    }

    fun onCanvasSizeChanged(
        width: Int,
        height: Int,
        radius: Float,
        radiusRatio: Float,
        onRadiusCalculated: (Float) -> Unit,
        onMaxBarHeightCalculated: (Float) -> Unit
    ) {
        onRadiusCalculated(
            if (radius == 0f) {
                (min(width, height) / 2f) * radiusRatio
            } else {
                radius
            }
        )

        onMaxBarHeightCalculated(
            height / MAX_BAR_HEIGHT_DIVISOR
        )
    }
}

internal object DrawSoundBarConstants {
    const val STROKE_WIDTH = 25f
}

internal object DrawSoundWaveStrokeConstants {
    const val STROKE_WIDTH = 6f
}
