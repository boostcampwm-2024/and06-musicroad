package com.squirtles.musicroad.detail.components.music.visualizer

data class VisualizerCallbacks(
    val onWaveCaptured: (ByteArray, Int) -> Unit = { _, _ -> },
    val onFftCaptured: (ByteArray, Int) -> Unit = { _, _ -> },
)
