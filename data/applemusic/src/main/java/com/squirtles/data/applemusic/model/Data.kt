package com.squirtles.data.applemusic.model

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val id: String,
    val attributes: Attributes,
)
