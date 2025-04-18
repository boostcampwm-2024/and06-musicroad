package com.miller198.audiovisualizer

import android.media.audiofx.Visualizer
import android.util.Log
import com.miller198.audiovisualizer.configs.VisualizerCallbacks

/**
 * Core class for capturing audio data using Android's [Visualizer] API.
 *
 * This class handles setup, configuration, and lifecycle management of a [Visualizer] instance.
 */
class BaseVisualizer {
    private var visualizer: Visualizer? = null

    /**
     * Initializes and starts the [Visualizer] with the given configuration.
     *
     * @param audioSessionId The audio session ID to capture audio from.
     * @param captureSize The number of bytes to capture. Must be a power of two and within supported range.
     * @param useWaveCapture Whether to enable waveform data capture.
     * @param useFftCapture Whether to enable FFT (frequency) data capture.
     * @param visualizerCallbacks Callbacks to receive waveform and/or FFT data.
     * @param captureRate Capture rate in milliseconds. Defaults to [Visualizer.getMaxCaptureRate].
     */
    fun start(
        audioSessionId: Int,
        captureSize: Int,
        useWaveCapture: Boolean,
        useFftCapture: Boolean,
        visualizerCallbacks: VisualizerCallbacks,
        captureRate: Int = Visualizer.getMaxCaptureRate(),
    ) {
        stop()
        setVisualizer(audioSessionId)

        // Validate capture size and fallback to max size if needed
        visualizer?.captureSize =
            if (!isPowerOfTwo(captureSize) || !isValidCaptureSize(captureSize)) {
                Visualizer.getCaptureSizeRange()[1].also {
                    Log.w("BaseVisualizer", "Invalid capture size, fallback to max: $it")
                }
            } else {
                captureSize
            }

        setVisualizerListener(
            useWaveCapture,
            useFftCapture,
            captureRate,
            visualizerCallbacks.provideVisualizerCallbacks()
        )
    }

    /**
     * Returns whether the visualizer is currently running.
     */
    fun isRunning(): Boolean = visualizer != null

    /**
     * Stops and releases the [Visualizer]
     */
    fun stop() {
        visualizer?.release()
        visualizer = null
    }

    /**
     * Creates and assigns a [Visualizer] instance for the given audio session.
     */
    private fun setVisualizer(audioSessionId: Int) {
        try {
            visualizer = Visualizer(audioSessionId)
        } catch (e: RuntimeException) {
            Log.e("BaseVisualizer", "Failed to create Visualizer", e)
        }
    }

    /**
     * Registers a [Visualizer.OnDataCaptureListener] to receive waveform and/or FFT data.
     */
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

    /**
     * Checks whether the provided [captureSize] is a power of two.
     */
    private fun isPowerOfTwo(captureSize: Int): Boolean {
        return captureSize > 0 && (captureSize and (captureSize - 1)) == 0
    }

    /**
     * Checks whether the provided [captureSize] is within the valid range supported by [Visualizer].
     */
    private fun isValidCaptureSize(captureSize: Int): Boolean {
        val range = Visualizer.getCaptureSizeRange()
        return captureSize in range[0]..range[1]
    }
}
