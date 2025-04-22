package com.squirtles.favorite

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFavoriteRepositoryImpl @Inject constructor(
    private val favoriteDataSource: FirebaseFavoriteDataSource
) : FirebaseFavoriteRepository {

    override suspend fun fetchIsFavorite(pickId: String, uid: String): Result<Boolean> {
        return favoriteDataSource.fetchIsFavorite(pickId, uid)
    }

    override suspend fun createFavorite(pickId: String, uid: String): Result<String> {
        return favoriteDataSource.createFavorite(pickId, uid)
    }

    override suspend fun deleteFavorite(pickId: String, uid: String): Result<String> {
        return favoriteDataSource.deleteFavorite(pickId, uid)
    }
}
