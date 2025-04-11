package com.squirtles.musicroad.detail.components.music.visualizer

import kotlin.math.hypot
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt

class FftDataProcessor {
    /**
     * this function must be used before applying other preprocessing functions
     *
     * Calculate the FFT magnitude of the audio data.
     * @param bytes The audio data as a byte array.
     * @return A list of FFT magnitudes.
     *
     * the first byte is DC, and the last byte is Nyquist, so drop them
     */
    fun calculateFftMagnitude(bytes: ByteArray): List<Float> {
        val audioData = bytes.drop(2).map { it.toDouble() }

        val size = audioData.size / 2
        val magnitudes = FloatArray(size)

        for (i in 0 until size) {
            val real = audioData.getOrNull(2 * i) ?: 0.0
            val imaginary = audioData.getOrNull(2 * i + 1) ?: 0.0
            magnitudes[i] = hypot(real, imaginary).toFloat()
        }

        return magnitudes.toList()
    }

    // 주파수 필터링
    fun filterFrequency(
        audioData: List<Float>,
        samplingRate: Int, // 48000
        captureSize: Int, // 1024
        minFreq: Int, // 40
        maxFreq: Int // 4500
    ): List<Float> {
        val resolution = (samplingRate / captureSize).toDouble()
        val startIndex = (minFreq / resolution).toInt()
        val endIndex = (maxFreq / resolution).toInt()

        return audioData.slice(startIndex..endIndex)
    }

    /* 주파수 대역별 가중치  */
    fun scaleFrequencies(audioData: List<Float>): List<Float> {
        val size = audioData.size
        return audioData.mapIndexed { index, value ->
            val scaleFactor = when {
                index < size / 8 -> 2.0f
                index < size / 4 -> 1.0f // 저주파 대역
                index < size / 2 -> 2.0f // 중간 대역
                index < size / 1.33 -> 3.0f // 고주파 대역
                else -> 4.0f // 고주파 대역
            }
            value * scaleFactor
        }
    }

    /**
     * Applies a logarithmic scale to the audio data.
     * @param audioData The audio data as a list of floats.
     * @return A list of logarithmically scaled audio data.
     */
    fun applyLogScale(
        audioData: List<Float>,
        base: Float = 10f,
        scaleFactor: Float = 1f
    ): List<Float> {
        val epsilon = 1e-6f // to avoid log(0)
        val minValue = audioData.minOrNull() ?: 0f
        val offset = if (minValue < 1f) 1f - minValue else 0f // move the minimum value to 1

        return audioData.map { value ->
            val shifted = value + offset + epsilon
            scaleFactor * (log(shifted, base))
        }
    }

    /**
     * Dynamic Range Compression
     * @param audioData The audio data as a list of floats.
     * @return A list of compressed audio data.
     */
    fun compressDynamicRangeRoot(audioData: List<Float>): List<Float> {
        return audioData.map { sqrt(it) }
    }

    /**
     *  Z-Score Normalization
     *  @param audioData The audio data as a list of floats.
     *  @return A list contains z-score normalized data with minus values
     */
    fun normalizeByZScore(audioData: List<Float>): List<Float> {
        val mean = audioData.average().toFloat()
        val stdDev = sqrt(audioData.map { (it - mean).pow(2) }.average()).toFloat()

        // if stdDev is 0 or NaN, return a list of zeros
        if (stdDev == 0f || stdDev.isNaN()) {
            return List(audioData.size) { 0f }
        }

        return audioData.map { (it - mean) / stdDev }
    }

    /**
     * Normalize the audio data between 0 and 1.
     * @param audioData The audio data as a list of floats.
     * @return A list of normalized audio data.
     */
    fun normalize(audioData: List<Float>): List<Float> {
        val max = audioData.maxOrNull() ?: 1f // 데이터 최대값
        val min = audioData.minOrNull() ?: 0f // 데이터 최소값
        return if (max - min <= 1f) List(audioData.size) { 0f } else audioData.map { (it - min) / (max - min) }
    }
}
