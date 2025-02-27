package com.squirtles.favorite

import com.squirtles.firebase.handleResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFavoriteRepositoryImpl @Inject constructor(
    private val favoriteDataSource: FirebaseFavoriteDataSource
) : FirebaseFavoriteRepository {

    override suspend fun fetchIsFavorite(pickId: String, userId: String): Result<Boolean> {
        return handleResult {
            favoriteDataSource.fetchIsFavorite(pickId, userId)
        }
    }

    override suspend fun createFavorite(pickId: String, userId: String): Result<Boolean> {
        return handleResult {
            favoriteDataSource.createFavorite(pickId, userId)
        }
    }

    override suspend fun deleteFavorite(pickId: String, userId: String): Result<Boolean> {
        return handleResult {
            favoriteDataSource.deleteFavorite(pickId, userId)
        }
    }
}
