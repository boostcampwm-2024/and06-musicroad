package com.squirtles.data.favorite

interface FirebaseFavoriteDataSource {
    suspend fun fetchIsFavorite(pickId: String, userId: String): Result<Boolean>
    suspend fun createFavorite(pickId: String, userId: String): Result<String>
    suspend fun deleteFavorite(pickId: String, userId: String): Result<String>
}
