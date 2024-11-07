package com.squirtles.data.datasource.remote.model.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    @SerialName("external_urls") val externalUrls: ExternalUrls,
    @SerialName("id") val id: String,
    @SerialName("images") val images: List<Image>? = null,
    @SerialName("name") val name: String,
)
