package com.squirtles.data.datasource.remote.model.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    @SerialName("artists") val artists: List<Artist>,
    @SerialName("external_urls") val externalUrls: ExternalUrls,
    @SerialName("id") val albumId: String,
    @SerialName("images") val imageList: List<Image>,
    @SerialName("name") val albumName: String,
)
