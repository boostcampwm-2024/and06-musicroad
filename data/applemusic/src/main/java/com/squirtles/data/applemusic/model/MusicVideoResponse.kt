package com.squirtles.data.applemusic.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicVideoResponse(
    val data: List<Data>,
)

