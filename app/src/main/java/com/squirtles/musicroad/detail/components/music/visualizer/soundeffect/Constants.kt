package com.squirtles.musicroad.detail.components.music.visualizer.soundeffect

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

internal object DrawSoundEffectConstants {
    val OFFSET_ANGLE = Math.toRadians(-90.0)
    const val GRADIENT_RADIUS_RATIO = 0.8f
    const val GRADIENT_DURATION = 2500

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
}

internal object DrawSoundBarConstants {
    const val STROKE_WIDTH = 25f
}

internal object DrawSoundWaveStrokeConstants {
    const val STROKE_WIDTH = 6f
}
