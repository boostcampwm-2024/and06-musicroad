package com.miller198.audio_visualizer.ui

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
import com.miller198.audio_visualizer.BaseVisualizer
import com.miller198.audio_visualizer.configs.ClippingRadiusConfig
import com.miller198.audio_visualizer.configs.GradientConfig
import com.miller198.audio_visualizer.configs.VisualizerCallbacks
import com.miller198.audio_visualizer.configs.VisualizerConfig
import com.miller198.audio_visualizer.soundeffect.DrawSoundEffectConfigs
import com.miller198.audio_visualizer.soundeffect.SoundEffects
import kotlinx.coroutines.launch

@Composable
fun CircleVisualizer(
    audioSessionId: Int,
    soundEffects: SoundEffects,
    visualizerConfig: VisualizerConfig,
    modifier: Modifier = Modifier,
    color: Color = White,
    innerRadiusConfig: ClippingRadiusConfig = ClippingRadiusConfig.FullClip,
    gradientConfig: GradientConfig = GradientConfig.Default,
) {
    val magnitudes = remember { mutableStateOf<List<Float>>(emptyList()) }
    val animateMagnitudes = remember { mutableStateOf<List<Animatable<Float, AnimationVector1D>>>(emptyList()) }
    val visualizer = remember { BaseVisualizer() }

    DrawSoundEffectConfigs.gradientConfig = gradientConfig
    DrawSoundEffectConfigs.innerRadiusConfig = innerRadiusConfig

    LaunchedEffect(audioSessionId) {
        visualizer.start(
            audioSessionId = audioSessionId,
            captureSize = visualizerConfig.captureSize,
            useWaveCapture = visualizerConfig.useWaveCapture,
            useFftCapture = visualizerConfig.useFftCapture,
            visualizerCallbacks = VisualizerCallbacks(
                onWaveCaptured = { visualizer, bytes, samplingRate ->
                    magnitudes.value = visualizerConfig.processWaveData?.invoke(visualizer, bytes, samplingRate) ?: emptyList()
                },
                onFftCaptured = { visualizer, bytes, samplingRate ->
                    magnitudes.value = visualizerConfig.processFftData?.invoke(visualizer, bytes, samplingRate) ?: emptyList()
                },
            )
        )
    }

    LaunchedEffect(magnitudes.value) {
        if (animateMagnitudes.value.isEmpty()) {
            animateMagnitudes.value = magnitudes.value.map { Animatable(it) }
        } else {
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

    DisposableEffect(audioSessionId) {
        onDispose {
            visualizer.stop()
        }
    }

    soundEffects.drawEffect.invoke(
        animateMagnitudes.value.map { it.value },
        color,
        modifier,
    )
}
