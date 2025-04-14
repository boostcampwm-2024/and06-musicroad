package com.squirtles.musicroad.detail.components.music.visualizer.ui

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
import androidx.compose.ui.unit.dp
import com.squirtles.musicroad.detail.components.music.visualizer.BaseVisualizer
import com.squirtles.musicroad.detail.components.music.visualizer.FftDataProcessor
import com.squirtles.musicroad.detail.components.music.visualizer.SoundEffects
import com.squirtles.musicroad.detail.components.music.visualizer.VisualizerCallbacks
import com.squirtles.musicroad.detail.components.music.visualizer.ui.VisualizerConstants.CAPTURE_SIZE
import com.squirtles.musicroad.detail.components.music.visualizer.ui.VisualizerConstants.MAX_FREQ
import com.squirtles.musicroad.detail.components.music.visualizer.ui.VisualizerConstants.MIN_FREQ
import kotlinx.coroutines.launch

@Composable
fun CircleVisualizer(
    audioSessionId: Int,
    soundEffects: SoundEffects,
    radiusRatio: Float = 1.0f,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val fftMagnitudes = remember { mutableStateOf<List<Float>>(emptyList()) } // 상태로 리스트 관리
    val animateMagnitudes = remember { mutableStateOf<List<Animatable<Float, AnimationVector1D>>>(emptyList()) }
    val visualizer = remember { BaseVisualizer() }
    val processor = remember { FftDataProcessor() }

    LaunchedEffect(audioSessionId) {
        visualizer.start(
            audioSessionId = audioSessionId,
            captureSize = CAPTURE_SIZE,
//            captureRate = Visualizer.getMaxCaptureRate()/2,
            isWaveCapture = false,
            isFftCapture = true,
            visualizerCallbacks = VisualizerCallbacks(
                onFftCaptured = { bytes, samplingRate ->
                    fftMagnitudes.value = preProcessFftData(bytes, samplingRate, processor)
                }
            )
        )
    }

    LaunchedEffect(fftMagnitudes.value) {
        if (animateMagnitudes.value.isEmpty()) {
            animateMagnitudes.value = fftMagnitudes.value.map { Animatable(it) }
        } else {
            fftMagnitudes.value.forEachIndexed { i, magnitude ->
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
        true,
        0.dp,
        radiusRatio,
        modifier
    )
}

private fun preProcessFftData(
    bytes: ByteArray,
    samplingRate: Int,
    processor: FftDataProcessor
): List<Float> {
    val magnitudes = processor.calculateFftMagnitude(bytes)

    val filteredMagnitudes = processor.filterFrequency(
        magnitudes,
        samplingRate / 1000,
        CAPTURE_SIZE,
        MIN_FREQ,
        MAX_FREQ
    )

    val logScaleData = processor.applyLogScale(filteredMagnitudes)
    val zScore = processor.normalizeByZScore(logScaleData)
    val normalizedData = processor.normalize(zScore)

    return normalizedData
}
