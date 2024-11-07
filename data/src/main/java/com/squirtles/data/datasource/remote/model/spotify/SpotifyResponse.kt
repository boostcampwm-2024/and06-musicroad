package com.squirtles.data.datasource.remote.model.spotify

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyResponse(
    val tracks: Tracks? = null,
)
