package com.miller198.audiovisualizer.configs

import android.media.audiofx.Visualizer

/**
 * A container for user-defined callback functions for [Visualizer.OnDataCaptureListener].
 *
 * @param onWaveCaptured Callback invoked when waveform (raw audio) data is captured.
 * @param onFftCaptured Callback invoked when FFT (frequency domain) data is captured.
 */
data class VisualizerCallbacks(
    val onWaveCaptured: (Visualizer, ByteArray, Int) -> Unit = { _, _, _ -> },
    val onFftCaptured: (Visualizer, ByteArray, Int) -> Unit = { _, _, _ -> },
) {
    /**
     * Provides an implementation of [Visualizer.OnDataCaptureListener] that delegates
     * waveform and FFT capture events to the corresponding user-defined callbacks.
     *
     * @return An instance of [Visualizer.OnDataCaptureListener] to be used with the Visualizer.
     */
    fun provideVisualizerCallbacks() = object : Visualizer.OnDataCaptureListener {
        override fun onWaveFormDataCapture(visualizer: Visualizer, bytes: ByteArray, samplingRate: Int) {
            onWaveCaptured(visualizer, bytes, samplingRate)
        }

        override fun onFftDataCapture(visualizer: Visualizer, bytes: ByteArray, samplingRate: Int) {
            onFftCaptured(visualizer, bytes, samplingRate)
        }
    }
}
