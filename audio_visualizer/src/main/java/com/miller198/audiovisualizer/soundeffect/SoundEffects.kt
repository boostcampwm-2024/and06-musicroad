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
    val title: String,
    val drawEffect: @Composable (
        audioData: List<Float>,
        color: Color,
        modifier: Modifier,
    ) -> Unit
) {
    /** No effect. This does not render any audio visualization. */
    NONE(
        title = "None",
        drawEffect = { _, _, _ -> }
    ),

    /** A vertical bar graph representation of the audio data. */
    BAR(
        title = "bar",
        drawEffect = { audioData, color, modifier ->
            DrawSoundBar(audioData, color, modifier)
        }
    ),

    /** A waveform rendered using stroke (outline only). */
    WAVE_STROKE(
        title = "stroke",
        drawEffect = { audioData, color, modifier ->
            DrawSoundWaveStroke(audioData, color, modifier)
        }
    ),

    /** A waveform rendered as a filled shape. */
    WAVE_FILL(
        title = "fill",
        drawEffect = { audioData, color, modifier ->
            DrawSoundWaveFill(audioData, color, modifier)
        }
    );

    companion object {
        fun getEffectNames() = entries.map { it.title }
        fun getEffectByTitle(title: String) = entries.find { it.title == title }
    }
}
