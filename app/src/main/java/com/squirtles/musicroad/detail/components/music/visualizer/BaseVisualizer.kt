package com.squirtles.musicroad.detail.components.music.visualizer

import android.media.audiofx.Visualizer
import android.util.Log

class BaseVisualizer {
    private var visualizer: Visualizer? = null

    fun start(
        audioSessionId: Int,
        captureSize: Int,
        captureRate: Int = Visualizer.getMaxCaptureRate(),
        isWaveCapture: Boolean,
        isFftCapture: Boolean,
        visualizerCallbacks: VisualizerCallbacks,
    ) {
        stop()
        setVisualizer(audioSessionId)
        // 캡처 사이즈 유효성 검사
        visualizer?.captureSize =
            if (!isPowerOfTwo(captureSize) || !isValidCaptureSize(captureSize)) {
                Visualizer.getCaptureSizeRange()[1].also {
                    Log.w("BaseVisualizer", "Invalid capture size, fallback to max: $it")
                }
            } else {
                captureSize
            }

        setVisualizerListener(
            isWaveCapture,
            isFftCapture,
            captureRate,
            provideVisualizerCallbacks(visualizerCallbacks)
        )
    }

    fun isRunning(): Boolean = visualizer != null

    fun stop() {
        visualizer?.release()
        visualizer = null
    }

    private fun setVisualizer(audioSessionId: Int) {
        try {
            visualizer = Visualizer(audioSessionId)
        } catch (e: RuntimeException) {
            Log.e("BaseVisualizer", "Failed to create Visualizer", e)
        }
    }

    private fun provideVisualizerCallbacks(
        visualizerCallbacks: VisualizerCallbacks,
    ) = object : Visualizer.OnDataCaptureListener {
        override fun onWaveFormDataCapture(visualizer: Visualizer, bytes: ByteArray, samplingRate: Int) {
            visualizerCallbacks.onWaveCaptured(bytes, samplingRate)
        }

        override fun onFftDataCapture(visualizer: Visualizer, bytes: ByteArray, samplingRate: Int) {
            visualizerCallbacks.onFftCaptured(bytes, samplingRate)
        }
    }

    private fun setVisualizerListener(
        isWaveCapture: Boolean,
        isFftCapture: Boolean,
        captureRate: Int,
        dataCaptureListener: Visualizer.OnDataCaptureListener,
    ) {
        visualizer?.run {
            enabled = false
            setDataCaptureListener(
                dataCaptureListener,
                captureRate,
                isWaveCapture,
                isFftCapture
            )
            enabled = true
        }
    }

    private fun isPowerOfTwo(n: Int): Boolean {
        return n > 0 && (n and (n - 1)) == 0
    }

    private fun isValidCaptureSize(captureSize: Int): Boolean {
        val range = Visualizer.getCaptureSizeRange()
        return captureSize in range[0]..range[1]
    }
}
