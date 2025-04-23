package com.squirtles.core.model

import kotlinx.serialization.Serializable

/**
 * 애플뮤직에서 불러온 노래 정보를 비즈니스 로직에서 사용하기 위해 변환한 클래스
 */
@Serializable
data class Song(
    val id: String,
    val songName: String,
    val artistName: String,
    val albumName: String,
    val imageUrl: String,
    val genreNames: List<String>,
    val bgColor: Int,
    val externalUrl: String,
    val previewUrl: String,
) {
    fun getImageUrlWithSize(width: Int, height: Int): String? {
        return if (imageUrl.isEmpty()) null
        else imageUrl.replace("{w}", width.toString())
            .replace("{h}", height.toString())
    }
}
