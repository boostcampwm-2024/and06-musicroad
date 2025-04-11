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
import com.squirtles.musicroad.detail.components.music.visualizer.BaseVisualizer
import com.squirtles.musicroad.detail.components.music.visualizer.FftDataProcessor
import com.squirtles.musicroad.detail.components.music.visualizer.VisualizerCallbacks
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundWaveFill
import com.squirtles.musicroad.detail.components.music.visualizer.ui.VisualizerConstants.CAPTURE_SIZE
import com.squirtles.musicroad.detail.components.music.visualizer.ui.VisualizerConstants.MAX_FREQ
import com.squirtles.musicroad.detail.components.music.visualizer.ui.VisualizerConstants.MIN_FREQ
import com.squirtles.musicroad.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun CircleVisualizer(
    audioSessionId: Int,
    color: Color = White,
    radiusRatio: Float,
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

    DrawSoundWaveFill(
        audioData = animateMagnitudes.value.map { it.value },
        color = color,
        radiusRatio = radiusRatio,
        useGradient = true,
        modifier = modifier
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

    val logScaleData = processor.applyLogScale(filteredMagnitudes, base = 2f)

    val zScore = processor.normalizeByZScore(logScaleData)
    val emphasizedPeaks = zScore.map { abs(it) }
    val normalizedData = processor.normalize(emphasizedPeaks)

    return normalizedData
}

