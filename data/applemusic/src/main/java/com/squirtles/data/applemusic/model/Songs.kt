package com.squirtles.data.applemusic.model

import kotlinx.serialization.Serializable

@Serializable
data class Songs(
    val next: String? = null,
    val data: List<Data>,
)
