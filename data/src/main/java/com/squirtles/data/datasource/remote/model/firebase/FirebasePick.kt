package com.squirtles.data.datasource.remote.model.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime

/**
 * Firestore에 저장된 pick document를 불러와 변환하기위한 데이터 클래스
 */
data class FirebasePick(
    val id: String? = null,
    val albumTitle: String? = null,
    val artists: List<String> = emptyList(),
    val trackTitle: String? = null,
    val geoHash: String? = null, // TODO: Firestore 데이터에도 추가해야함
    val location: GeoPoint? = null,
    val comment: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val createdBy: String? = null,
    val favoriteCount: Int = 0,
    val imageUrl: String? = null,
    val previewUrl: String? = null,
    val externalUrl: String? = null,
)

//data class FirebasePick(
//    @PropertyName("id") val id: String? = null,
//    @PropertyName("album_title") val albumTitle: String? = null,
//    @PropertyName("artists") val artists: List<String> = emptyList(),
//    @PropertyName("track_title") val trackTitle: String? = null,
//    @PropertyName("geo_hash") val geoHash: String? = null, // TODO: Firestore 데이터에도 추가해야함
//    @PropertyName("location") val location: GeoPoint? = null,
//    @PropertyName("comment") val comment: String? = null,
//    @PropertyName("created_at") val createdAt: Timestamp,
//    @PropertyName("created_by") val createdBy: String? = null,
//    @PropertyName("favorite_count") val favoriteCount: Int = 0,
//    @PropertyName("image_url") val imageUrl: String? = null,
//    @PropertyName("preview_url") val previewUrl: String? = null,
//    @PropertyName("external_url") val externalUrl: String? = null
//)
