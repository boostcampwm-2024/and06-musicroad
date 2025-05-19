package com.miller198.audiovisualizer.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import com.miller198.audiovisualizer.BaseVisualizer
import com.miller198.audiovisualizer.configs.VisualizerConfig
import com.miller198.audiovisualizer.soundeffect.DrawSoundEffectConfigs
import com.miller198.audiovisualizer.configs.GradientConfig
import com.miller198.audiovisualizer.configs.ClippingRadiusConfig
import com.miller198.audiovisualizer.configs.VisualizerCallbacks
import com.miller198.audiovisualizer.soundeffect.SoundEffects
import kotlinx.coroutines.launch

/**
 * Composable that visualizes audio data as a circular sound effect (e.g., wave or bar),
 * using a provided audio session ID.
 *
 * @param audioSessionId The audio session ID from the audio output source (e.g., MediaPlayer).
 * @param soundEffects Object that defines how to draw the sound effect. use [SoundEffects] (waveform, bars, etc.).
 * @param visualizerConfig Configuration for how the visualizer captures and processes audio data [VisualizerConfig].
 * @param modifier Modifier to apply to the visualizer's layout.
 * @param color The primary color to use for drawing the sound visualization.
 * @param clippingRadiusConfig Configuration for the inner clipping radius of the circle visualization. Default is [ClippingRadiusConfig.FullClip].
 * @param gradientConfig Configuration for gradient animation within the visualizer (optional). Default is [GradientConfig.Default] which uses gradient animation.
 */
@Composable
fun CircleVisualizer(
    audioSessionId: Int,
    soundEffects: SoundEffects,
    visualizerConfig: VisualizerConfig,
    modifier: Modifier = Modifier,
    color: Color = White,
    clippingRadiusConfig: ClippingRadiusConfig = ClippingRadiusConfig.FullClip,
    gradientConfig: GradientConfig = GradientConfig.Default,
) {
    /** Holds the current list of magnitude values */
    val magnitudes = remember { mutableStateOf<List<Float>>(emptyList()) }

    /** Animated version of the magnitudes for smooth transitions in visualization */
    val animateMagnitudes = remember { mutableStateOf<List<Animatable<Float, AnimationVector1D>>>(emptyList()) }

    val visualizer = remember { BaseVisualizer() }

    // Set global visual configuration (used in other rendering composable functions)
    DrawSoundEffectConfigs.gradientConfig = gradientConfig
    DrawSoundEffectConfigs.clippingRadiusConfig = clippingRadiusConfig

    // Start the visualizer when the composable is composed with the given audio session ID
    LaunchedEffect(audioSessionId) {
        visualizer.start(
            audioSessionId = audioSessionId,
            captureSize = visualizerConfig.captureSize,
            useWaveCapture = visualizerConfig.useWaveCapture,
            useFftCapture = visualizerConfig.useFftCapture,
            visualizerCallbacks = VisualizerCallbacks(
                // Callback for waveform audio data (optional processing)
                onWaveCaptured = { visualizer, bytes, samplingRate ->
                    magnitudes.value = visualizerConfig.processWaveData?.invoke(visualizer, bytes, samplingRate) ?: emptyList()
                },
                // Callback for FFT audio data (optional processing)
                onFftCaptured = { visualizer, bytes, samplingRate ->
                    magnitudes.value = visualizerConfig.processFftData?.invoke(visualizer, bytes, samplingRate) ?: emptyList()
                },
            )
        )
    }

    // Animate changes in magnitude values for smoother rendering transitions
    LaunchedEffect(magnitudes.value) {
        if (animateMagnitudes.value.isEmpty()) {
            // Initialize the animatable list if not already done
            animateMagnitudes.value = magnitudes.value.map { Animatable(it) }
        } else {
            // Animate each value to its new magnitude
            magnitudes.value.forEachIndexed { i, magnitude ->
                launch {
                    animateMagnitudes.value[i].animateTo(
                        targetValue = magnitude,
                        animationSpec = tween(
                            durationMillis = 120,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }
    }

    // Release the visualizer when composable leaves the composition
    DisposableEffect(audioSessionId) {
        onDispose {
            visualizer.stop()
        }
    }

    // Draw the sound effect using the provided drawEffect lambda
    soundEffects.drawEffect.invoke(
        animateMagnitudes.value.map { it.value },
        color,
        modifier,
    )
}
