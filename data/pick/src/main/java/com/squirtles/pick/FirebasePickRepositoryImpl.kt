package com.squirtles.pick

import com.squirtles.firebase.FirebaseException
import com.squirtles.firebase.handleResult
import com.squirtles.model.Pick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePickRepositoryImpl @Inject constructor(
    private val pickDataSource: FirebasePickDataSource
) : FirebasePickRepository {

    override suspend fun createPick(pick: Pick): Result<String> {
        return handleResult {
            pickDataSource.createPick(pick)
        }
    }

    override suspend fun deletePick(pickId: String, userId: String): Result<Boolean> {
        return handleResult {
            pickDataSource.deletePick(pickId, userId)
        }
    }

    override suspend fun fetchPick(pickID: String): Result<Pick> {
        return handleResult(FirebaseException.NoSuchPickException()) {
            pickDataSource.fetchPick(pickID)
        }
    }

    override suspend fun fetchMyPicks(userId: String): Result<List<Pick>> {
        return handleResult {
            pickDataSource.fetchMyPicks(userId)
        }
    }

    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): Result<List<Pick>> {
        val pickList = pickDataSource.fetchPicksInArea(lat, lng, radiusInM)
        return handleResult(FirebaseException.NoSuchPickInRadiusException()) {
            pickList.ifEmpty { null }
        }
    }

    override suspend fun fetchFavoritePicks(userId: String): Result<List<Pick>> {
        return handleResult {
            pickDataSource.fetchFavoritePicks(userId)
        }
    }
}
