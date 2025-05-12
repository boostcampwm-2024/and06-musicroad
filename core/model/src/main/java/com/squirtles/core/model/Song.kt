package com.squirtles.core.model

import kotlinx.serialization.Serializable
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

    fun encoded(): Song = this.copy(
        songName = URLEncoder.encode(songName, StandardCharsets.UTF_8.toString()).replace("+", "%20"),
        artistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8.toString()).replace("+", "%20"),
        albumName = URLEncoder.encode(albumName, StandardCharsets.UTF_8.toString()).replace("+", "%20"),
        imageUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString()).replace("+", "%20"),
        previewUrl = URLEncoder.encode(previewUrl, StandardCharsets.UTF_8.toString()).replace("+", "%20"),
        externalUrl = URLEncoder.encode(externalUrl, StandardCharsets.UTF_8.toString()).replace("+", "%20"),
        genreNames = genreNames.map {
            URLEncoder.encode(it, StandardCharsets.UTF_8.toString()).replace("+", "%20")
        },
    )
}
