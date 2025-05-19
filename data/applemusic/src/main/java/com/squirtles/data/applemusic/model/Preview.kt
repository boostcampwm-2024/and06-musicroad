package com.squirtles.data.applemusic.model

import kotlinx.serialization.Serializable

@Serializable
data class Preview(
    val url: String? = null,
    val hlsUrl: String? = null,
    val artwork: Artwork? = null,
)
