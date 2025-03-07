package com.squirtles.favorite

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFavoriteRepositoryImpl @Inject constructor(
    private val favoriteDataSource: FirebaseFavoriteDataSource
) : FirebaseFavoriteRepository {

    override suspend fun fetchIsFavorite(pickId: String, userId: String): Result<Boolean> {
        return favoriteDataSource.fetchIsFavorite(pickId, userId)
    }

    override suspend fun createFavorite(pickId: String, userId: String): Result<String> {
        return favoriteDataSource.createFavorite(pickId, userId)
    }

    override suspend fun deleteFavorite(pickId: String, userId: String): Result<String> {
        return favoriteDataSource.deleteFavorite(pickId, userId)
    }
}
