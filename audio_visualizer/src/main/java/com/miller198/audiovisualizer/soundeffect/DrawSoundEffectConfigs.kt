package com.miller198.audiovisualizer.soundeffect

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.miller198.audiovisualizer.configs.ClippingRadiusConfig
import com.miller198.audiovisualizer.configs.GradientConfig
import kotlin.math.min

/**
 * Configuration and utility object for controlling the sound effect drawing behavior.
 */
object DrawSoundEffectConfigs {

    /** Ratio used to calculate the gradient radius relative to the maximum effect radius. */
    const val GRADIENT_RADIUS_RATIO = 0.8f

    /** Divisor used to determine the maximum height of the visual wave effect. */
    const val EFFECT_HEIGHT_DIVISOR = 5f

    /** Current gradient configuration. Default is [GradientConfig.Default] */
    internal var gradientConfig: GradientConfig = GradientConfig.Default

    /**
     * Current clipping radius configuration. Controls the inner radius of the sound wave.
     * Default is [ClippingRadiusConfig.FullClip]
     */
    internal var clippingRadiusConfig: ClippingRadiusConfig = ClippingRadiusConfig.FullClip

    /**
     * Animated radius value for a radial gradient brush.
     * Only animates if [GradientConfig.useGradient] of [gradientConfig] is true.
     *
     * @param easing Easing function used in the animation.
     * @return Current animated radius value.
     */
    val animatedGradientRadius: @Composable (Easing) -> Float = { easing ->
        if (gradientConfig.useGradient) {
            val transition = rememberInfiniteTransition(label = "GradientRadiusTransition")
            transition.animateFloat(
                initialValue = 0.01f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = gradientConfig.duration, easing = easing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "AnimatedGradientRadius"
            ).value
        } else {
            1f
        }
    }

    /**
     * Called when the canvas size changes to update core dimensions like inner radius and maximum effect height.
     *
     * @param width New width of the canvas.
     * @param height New height of the canvas.
     * @param onRadiusCalculated Callback to provide the computed inner radius.
     * @param onMaxEffectHeightCalculated Callback to provide the computed max wave height.
     */
    internal fun onCanvasSizeChanged(
        width: Int,
        height: Int,
        onRadiusCalculated: (Float) -> Unit,
        onMaxEffectHeightCalculated: (Float) -> Unit
    ) {
        val clippingRadius = clippingRadiusConfig.dp.value
        val clippingRadiusRatio = clippingRadiusConfig.ratio

        onRadiusCalculated(
            if (clippingRadius > 0f) clippingRadius else (min(width, height) / 2) * clippingRadiusRatio
        )

        onMaxEffectHeightCalculated(
            min(width, height) / EFFECT_HEIGHT_DIVISOR
        )
    }
}

/**
 * Constants related to drawing the sound bars (e.g., circular bars).
 */
internal object DrawSoundBarConstants {
    /** Default stroke width for drawing sound bars. */
    const val STROKE_WIDTH = 25f
}

/**
 * Constants related to drawing stroke-style sound waves.
 */
internal object DrawSoundWaveStrokeConstants {
    /** Default stroke width for drawing wave outlines. */
    const val STROKE_WIDTH = 6f
}
