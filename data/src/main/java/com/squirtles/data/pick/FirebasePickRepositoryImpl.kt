package com.squirtles.data.pick

import com.squirtles.data.firebase.FirebaseException
import com.squirtles.data.firebase.handleResult
import com.squirtles.domain.model.Pick
import com.squirtles.domain.pick.FirebasePickDataSource
import com.squirtles.domain.pick.FirebasePickRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePickRepositoryImpl @Inject constructor(
    private val pickDataSource: FirebasePickDataSource
) : FirebasePickRepository {

    override suspend fun createPick(pick: Pick): Result<String> {
        return pickDataSource.createPick(pick)
    }

    override suspend fun deletePick(pickId: String, userId: String): Result<String> {
        return pickDataSource.deletePick(pickId, userId)
    }

    override suspend fun fetchPick(pickID: String): Result<Pick> {
        return pickDataSource.fetchPick(pickID)
    }

    override suspend fun fetchMyPicks(userId: String): Result<List<Pick>> {
        return pickDataSource.fetchMyPicks(userId)
    }

    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): Result<List<Pick>> {
        val pickList = pickDataSource.fetchPicksInArea(lat, lng, radiusInM)
        return handleResult(FirebaseException.NoSuchPickInRadiusException()) {
            pickList.getOrThrow().ifEmpty { null }
        }
    }

    override suspend fun fetchFavoritePicks(userId: String): Result<List<Pick>> {
        return pickDataSource.fetchFavoritePicks(userId)
    }
}
