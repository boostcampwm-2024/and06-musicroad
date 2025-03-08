package com.squirtles.domain.favorite

import com.squirtles.domain.model.Pick

interface FirebaseFavoriteRepository {
    suspend fun fetchIsFavorite(pickId: String, userId: String): Result<Boolean>
    suspend fun createFavorite(pickId: String, userId: String): Result<String>
    suspend fun deleteFavorite(pickId: String, userId: String): Result<String>
}
