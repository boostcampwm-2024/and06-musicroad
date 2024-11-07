package com.squirtles.data.datasource.remote.model.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Tracks(
    @SerialName("items") val items: List<Track>,
    @SerialName("limit") val limit: Int,
    @SerialName("next") val next: String?, // offset 기준 다음 페이지
    @SerialName("offset") val offset: Int,
    @SerialName("previous") val previous: String?, // offset 기준 이전 페이지
    @SerialName("total") val total: Int // 검색 결과 총 개수
)
