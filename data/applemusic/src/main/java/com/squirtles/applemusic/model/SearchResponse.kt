package com.squirtles.applemusic.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val results: Results,
)
