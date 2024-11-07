package com.squirtles.data.datasource.remote.model.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    @SerialName("album") val albumInfo: Album,
    @SerialName("artists") val artistList: List<Artist>,
    @SerialName("disc_number") val discNumber: Int,
    @SerialName("duration_ms") val durationInMilliseconds: Int, // 트랙 길이
    @SerialName("external_urls") val externalUrl: ExternalUrls,
    @SerialName("id") val trackId: String,
    @SerialName("name") val trackName: String,
    @SerialName("popularity") val popularityScore: Int,
    @SerialName("preview_url") val previewUrl: String?, // 없을수있음
)
