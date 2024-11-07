package com.squirtles.data.mapper

import com.squirtles.data.datasource.remote.model.firebase.FirebasePick
import com.squirtles.domain.model.GeoPoint
import com.squirtles.domain.model.Pick

internal fun FirebasePick.toPick(): Pick = Pick(
    id = id.toString(),
    albumTitle = albumTitle.toString(),
    artists = artists,
    songTitle = trackTitle.toString(),
    location = GeoPoint(1.0, 1.0), // TODO: geohash -> GeoPoint 변환 정의 후 변경
    comment = comment.toString(),
    createdAt = createdAt.seconds,
    createdBy = createdBy.toString(),
    favoriteCount = favoriteCount,
    imageUrl = imageUrl.toString(),
    previewUrl = previewUrl.toString(),
    externalUrl = externalUrl.toString(),
)