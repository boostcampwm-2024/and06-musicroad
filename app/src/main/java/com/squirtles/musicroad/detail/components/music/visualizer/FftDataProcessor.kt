package com.squirtles.musicroad.detail.components.music.visualizer

import android.media.audiofx.Visualizer
import kotlin.math.hypot
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sqrt

class FftDataProcessor {
    /**
     * **This function must be used before applying any other preprocessing functions.**
     *
     * Calculates the FFT (Fast Fourier Transform) magnitude spectrum from raw FFT byte data.
     *
     * @param bytes The audio data as a byte array captured by [Visualizer.OnDataCaptureListener.onFftDataCapture].
     *              The array alternates real and imaginary parts: [real0, imag0, real1, imag1, ...].
     *
     * @return A list of FFT magnitudes (in linear scale), excluding the first two values (DC and Nyquist).
     *
     * Note: The first two bytes represent the DC component and the Nyquist frequency and are excluded.
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

    /**
     * Filters the given frequency spectrum data to include only the components within a specified frequency range.
     *
     * @param audioData The FFT magnitude data as a list of Floats.
     * @param samplingRate The sampling rate of the original audio signal (in Hz).
     * @param captureSize The size of the FFT window used when capturing the audio data.
     * @param minFreq The minimum frequency (in Hz) to include in the result.
     * @param maxFreq The maximum frequency (in Hz) to include in the result.
     *
     * @return A list of magnitudes corresponding to the frequency components within [minFreq, maxFreq].
     *
     * The frequency resolution is calculated as `samplingRate / captureSize`, and the corresponding index
     * range is computed to extract the subset of the frequency data.
     */
    fun filterFrequency(
        audioData: List<Float>,
        samplingRate: Int,
        captureSize: Int,
        minFreq: Int,
        maxFreq: Int
    ): List<Float> {
        val resolution = ((samplingRate / 2.0) / (captureSize / 2 - 1))
        val startIndex = (minFreq / resolution).toInt().coerceIn(0, audioData.lastIndex)
        val endIndex = (maxFreq / resolution).toInt().coerceIn(startIndex, audioData.lastIndex)

        return audioData.slice(startIndex..endIndex)
    }

    /**
     * Applies scaling weights to audio frequency data based on predefined frequency range ratios.
     *
     * Each element in the audio data is scaled according to which ratio range it falls into,
     * as defined by the provided list of [FrequencyScale]s.
     *
     * @param audioData A list of Float values representing the frequency domain data (e.g., FFT results).
     * @param frequencyScales A list of [FrequencyScale] objects defining the ratio ranges and their associated weights.
     *
     * @return A new list of Float values with each element scaled according to its frequency range.
     */
    fun scaleFrequencies(
        audioData: List<Float>,
        frequencyScales: List<FrequencyScale>
    ): List<Float> {
        val size = audioData.size
        return audioData.mapIndexed { index, value ->
            val ratio = index.toFloat() / size
            val scale = frequencyScales.find { ratio in it.rangeRatio }?.weight ?: 1.0f
            value * scale
        }
    }

    /**
     * Applies a logarithmic scale transformation to the given audio magnitude data.
     * for compressing large dynamic ranges in audio signals,
     *
     * A small epsilon is added to avoid log(0), and an offset is used to ensure
     * that all input values are positive before applying the logarithm.
     *
     * @param audioData The raw audio magnitude data (e.g., from FFT) as a list of floats.
     * @param base The base of the logarithm to apply (must be > 0 and ≠ 1; default is 10).
     * @param scaleFactor A multiplier applied after the logarithm for additional scaling (default is 1).
     * @return A list of audio magnitudes scaled logarithmically.
     */
    fun applyLogScale(
        audioData: List<Float>,
        base: Float = 10f,
        scaleFactor: Float = 1f
    ): List<Float> {
        require(base > 0f && base != 1f) { "Logarithm base must be greater than 0 and not equal to 1." }

        val epsilon = 1e-6f // to avoid log(0)
        val minValue = audioData.minOrNull() ?: 0f
        val offset = if (minValue < 1f) 1f - minValue else 0f // move the minimum value to 1

        return audioData.map { value ->
            val shifted = value + offset + epsilon
            val safeValue = if (shifted <= 0f || shifted.isNaN()) epsilon else shifted
            scaleFactor * log(safeValue, base)
        }
    }

    /**
     * Applies dynamic range compression using square root scaling.
     *
     * This method is useful for reducing the impact of high-magnitude peaks
     * and enhancing lower-magnitude values, making the overall data more perceptually uniform.
     *
     * @param audioData The audio data as a list of floats (typically FFT magnitudes).
     * @return A list of compressed audio data using sqrt scaling.
     *
     * Note: Negative input values are clamped to 0 to avoid sqrt domain errors.
     */
    fun compressDynamicRangeRoot(audioData: List<Float>): List<Float> {
        return audioData.map { value ->
            sqrt(value.coerceAtLeast(0f))
        }
    }

    /**
     * Applies Z-Score normalization to the given audio data.
     *
     * This method standardizes the data to have zero mean and unit variance,
     * making it easier to compare values across different datasets or features.
     *
     * @param audioData The audio data as a list of floats.
     * @return A list of normalized values centered around 0 with unit variance.
     *         If input is empty or standard deviation is zero, returns a list of zeros.
     */
    fun normalizeByZScore(audioData: List<Float>): List<Float> {
        if (audioData.isEmpty()) return emptyList()

        val mean = audioData.average().toFloat()
        val stdDev = sqrt(audioData.map { (it - mean).pow(2) }.average()).toFloat()

        // if stdDev is 0 or NaN, return a list of zeros
        if (stdDev == 0f || stdDev.isNaN()) {
            return List(audioData.size) { 0f }
        }

        return audioData.map { (it - mean) / stdDev }
    }

    /**
     * Applies Max-Min Normalization to the given audio data.
     *
     * @param audioData The audio data as a list of floats.
     * @return A list of normalized values between 0 and 1.
     */
    fun normalize(audioData: List<Float>): List<Float> {
        val max = audioData.maxOrNull() ?: 1f // 데이터 최대값
        val min = audioData.minOrNull() ?: 0f // 데이터 최소값
        return if (max - min <= 1f) List(audioData.size) { 0f } else audioData.map { (it - min) / (max - min) }
    }
}
