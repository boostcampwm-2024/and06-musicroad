package com.squirtles.core.model

import java.time.LocalDate

data class MusicVideo(
    val id: String,
    val songName: String,
    val artistName: String,
    val albumName: String,
    val releaseDate: LocalDate,
    val previewUrl: String,
    val thumbnailUrl: String
)
