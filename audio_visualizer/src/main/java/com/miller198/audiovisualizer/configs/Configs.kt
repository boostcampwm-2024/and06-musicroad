package com.miller198.audiovisualizer.configs

import android.media.audiofx.Visualizer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miller198.audiovisualizer.defaultPreProcessFftData

/**
 * Interface for configuring the audio visualizer.
 * Supports custom, FFT, and waveform capture configurations.
 */
sealed interface VisualizerConfig {

    /** Whether to use waveform (time domain) capture */
    val useWaveCapture: Boolean

    /** Whether to use FFT (frequency domain) capture */
    val useFftCapture: Boolean

    /** Buffer size for audio capture (e.g., 512, 1024) */
    val captureSize: Int

    /** Optional FFT data processing function */
    val processFftData: ((Visualizer, ByteArray, Int) -> List<Float>)?

    /** Optional waveform data processing function */
    val processWaveData: ((Visualizer, ByteArray, Int) -> List<Float>)?

    /** Fully custom configuration defined by the user. */
    data class Custom(
        override val useWaveCapture: Boolean,
        override val useFftCapture: Boolean,
        override val captureSize: Int,
        override val processFftData: ((Visualizer, ByteArray, Int) -> List<Float>)?,
        override val processWaveData: ((Visualizer, ByteArray, Int) -> List<Float>)?
    ) : VisualizerConfig

    /** FFT-based visualizer configuration. */
    data class FftCaptureConfig(
        val minFrequency: Int, // Minimum frequency to analyze (Hz)
        val maxFrequency: Int, // Maximum frequency to analyze (Hz)
        override val captureSize: Int,
        override val processFftData: (Visualizer, ByteArray, Int) -> List<Float>,
    ) : VisualizerConfig {
        override val useWaveCapture: Boolean = false
        override val useFftCapture: Boolean = true
        override val processWaveData: ((Visualizer, ByteArray, Int) -> List<Float>)? = null

        init {
            require(minFrequency >= 0 && maxFrequency >= 0) {
                throw IllegalArgumentException("Minimum and maximum frequencies must be non-negative.")
            }
            require(minFrequency <= maxFrequency) {
                throw IllegalArgumentException("Minimum frequency must be less than or equal to maximum frequency.")
            }
        }

        /** Default FFT configuration. */
        companion object {
            const val DEFAULT_MIN_FREQ = 40
            const val DEFAULT_MAX_FREQ = 4000
            const val DEFAULT_CAPTURE_SIZE = 1024

            val Default = FftCaptureConfig(
                minFrequency = DEFAULT_MIN_FREQ,
                maxFrequency = DEFAULT_MAX_FREQ,
                captureSize = DEFAULT_CAPTURE_SIZE,
                processFftData = { _, byteArray, samplingRate ->
                    // Default FFT preprocessing (custom implementation required)
                    defaultPreProcessFftData(byteArray, DEFAULT_CAPTURE_SIZE, DEFAULT_MIN_FREQ, DEFAULT_MAX_FREQ, samplingRate)
                }
            )
        }
    }

    /**
     * Waveform-based visualizer configuration.
     */
    open class WaveCaptureConfig(
        override val captureSize: Int,
        override val processWaveData: (Visualizer, ByteArray, Int) -> List<Float>
    ) : VisualizerConfig {
        override val useWaveCapture: Boolean = true
        override val useFftCapture: Boolean = false
        override val processFftData: ((Visualizer, ByteArray, Int) -> List<Float>)? = null

        /** Default waveform configuration. */
        data object Default : WaveCaptureConfig(
            captureSize = 1024,
            processWaveData = { _, _, _ ->
                // TODO: Implement default waveform preprocessing logic
                emptyList()
            }
        )
    }
}

/**
 * Interface for gradient animation configuration.
 */
sealed interface GradientConfig {
    val useGradient: Boolean      // Whether gradient is enabled
    val duration: Int             // Animation duration in milliseconds
    val color: Color              // Base color of the gradient

    /** Default gradient config (enabled). */
    data object Default : GradientConfig {
        override val useGradient: Boolean = true
        override val duration: Int = 2500
        override val color: Color = White
    }

    /** Enabled gradient config with custom options. */
    data class Enabled(
        override val duration: Int,
        override val color: Color,
    ) : GradientConfig {
        override val useGradient: Boolean = true
    }

    /** Disabled gradient config (no animation, transparent color) */
    data object Disabled : GradientConfig {
        override val useGradient: Boolean = false
        override val duration: Int = 0
        override val color: Color = Color.Transparent
    }
}

/**
 * Represents configuration options for determining the inner clipping radius of a visual element
 * (such as the inner circle in an audio visualization).
 *
 * Implementations specify either a fixed radius in Dp, a relative ratio of the canvas radius,
 * or a default value.
 *
 * @property dp The fixed radius in density-independent pixels (Dp). If greater than 0, this takes priority over [ratio].
 * @property ratio A relative ratio (0.0 to 1.0) used when [dp] is zero. Represents a proportion of the canvas radius.
 */
sealed interface ClippingRadiusConfig {
    val dp: Dp
    val ratio: Float

    /**
     * Fixed radius configuration using a specific dp value.
     * If this is used, the ratio is ignored.
     *
     * @param dp Must be >= 0.0
     */
    data class Fixed(override val dp: Dp) : ClippingRadiusConfig {
        override val ratio: Float = 0f

        init {
            require(dp.value >= 0f) {
                throw IllegalArgumentException("dp must be >= 0, but was ${dp.value}")
            }
        }
    }

    /**
     * Ratio-based radius configuration using a value between 0.0 and 1.0.
     * Represents a percentage of the canvas radius.
     *
     * @param ratio Must be in the range [0.0, 1.0]
     */
    data class Ratio(override val ratio: Float) : ClippingRadiusConfig {
        override val dp: Dp = 0.dp

        init {
            require(ratio in 0f..1f) {
                throw IllegalArgumentException("ratio must be in the range [0, 1], but was $ratio")
            }
        }
    }

    /**
     * Default configuration: ratio = 1.0 (full canvas radius), dp = 0.dp.
     * This means inner clipping is applied maximally.
     */
    data object FullClip : ClippingRadiusConfig {
        override val dp: Dp = 0.dp
        override val ratio: Float = 1f
    }

    /**
     * No clipping applied: ratio = 0.0, dp = 0.dp.
     * The content starts from the edge of the canvas, without an inner gap.
     */
    data object NoClip : ClippingRadiusConfig {
        override val dp: Dp = 0.dp
        override val ratio: Float = 0f
    }

    /**
     * Small inner clipping applied: ratio = 0.3, dp = 0.dp.
     * Leaves a small circular gap in the center.
     */
    data object Small : ClippingRadiusConfig {
        override val dp: Dp = 0.dp
        override val ratio: Float = 0.3f
    }

    /**
     * Medium inner clipping applied: ratio = 0.7, dp = 0.dp.
     * Leaves a medium-sized circular gap in the center.
     */
    data object Medium : ClippingRadiusConfig {
        override val dp: Dp = 0.dp
        override val ratio: Float = 0.7f
    }
}
