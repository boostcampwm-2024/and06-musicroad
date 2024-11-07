package com.squirtles.domain.model

import java.time.LocalDateTime

/**
 * 앱에서 사용하기 위한 Pick 정보 데이터클래스
 */
data class Pick(
    val id: String,
    val albumTitle: String,
    val artists: List<String>,
    val songTitle: String,
    val location: GeoPoint,
    val comment: String,
    val createdAt: Long,
    val createdBy: String,
    val favoriteCount: Int = 0,
    val imageUrl: String,
    val previewUrl: String,
    val externalUrl: String,
)

data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)