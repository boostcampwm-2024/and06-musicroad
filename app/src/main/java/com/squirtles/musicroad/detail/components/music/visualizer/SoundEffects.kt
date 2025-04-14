package com.squirtles.musicroad.detail.components.music.visualizer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundBar
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundWaveFill
import com.squirtles.musicroad.detail.components.music.visualizer.soundeffect.DrawSoundWaveStroke

enum class SoundEffects(
    val drawEffect: @Composable (
        audioData: List<Float>,
        color: Color,
        useGradient: Boolean,
        radius: Dp,
        radiusRatio: Float,
        modifier: Modifier
    ) -> Unit
) {
    BAR({ audioData, color, useGradient, radius, radiusRatio, modifier ->
        DrawSoundBar(audioData, color, useGradient, radius, radiusRatio, modifier)
    }),
    WAVE_STROKE({ audioData, color, useGradient, radius, radiusRatio, modifier ->
        DrawSoundWaveStroke(audioData, color, useGradient, radius, radiusRatio, modifier)
    }),
    WAVE_FILL({ audioData, color, useGradient, radius, radiusRatio, modifier ->
        DrawSoundWaveFill(audioData, color, useGradient, radius, radiusRatio, modifier)
    })
}
