package com.squirtles.domain.favorite

interface FirebaseFavoriteRepository {
    suspend fun fetchIsFavorite(pickId: String, uid: String): Result<Boolean>
    suspend fun createFavorite(pickId: String, uid: String): Result<String>
    suspend fun deleteFavorite(pickId: String, uid: String): Result<String>
}
