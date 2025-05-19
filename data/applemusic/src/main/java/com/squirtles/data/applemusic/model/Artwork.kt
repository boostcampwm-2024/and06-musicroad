package com.squirtles.data.applemusic.model

import kotlinx.serialization.Serializable

@Serializable
data class Artwork(
    val width: Int,
    val height: Int,
    val url: String,
    val bgColor: String? = null
)
