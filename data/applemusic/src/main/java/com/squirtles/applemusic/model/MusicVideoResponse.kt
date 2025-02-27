package com.squirtles.applemusic.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicVideoResponse(
    val data: List<Data>,
)

