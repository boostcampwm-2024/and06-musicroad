package com.miller198.audiovisualizer.soundeffect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Enum representing different sound visualization effects.
 * Each effect defines a Composable function that draws the audio data using a specific visual style.
 *
 * @property drawEffect A Composable lambda that renders the corresponding sound effect.
 */
enum class SoundEffects(
    val drawEffect: @Composable (
        audioData: List<Float>,
        color: Color,
        modifier: Modifier,
    ) -> Unit
) {
    /** No effect. This does not render any audio visualization. */
    NONE({ _, _, _ -> }),

    /** A vertical bar graph representation of the audio data. */
    BAR({ audioData, color, modifier ->
        DrawSoundBar(audioData, color, modifier)
    }),

    /** A waveform rendered using stroke (outline only). */
    WAVE_STROKE({ audioData, color, modifier ->
        DrawSoundWaveStroke(audioData, color, modifier)
    }),

    /** A waveform rendered as a filled shape. */
    WAVE_FILL({ audioData, color, modifier ->
        DrawSoundWaveFill(audioData, color, modifier)
    })
}
