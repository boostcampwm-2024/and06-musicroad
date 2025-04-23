package com.squirtles.data.pick

import com.squirtles.domain.pick.FirebasePickRepository
import com.squirtles.data.firebase.model.toFirebasePick
import com.squirtles.data.firebase.model.toPick
import com.squirtles.core.model.Pick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePickRepositoryImpl @Inject constructor(
    private val pickDataSource: FirebasePickDataSource
) : FirebasePickRepository {

    override suspend fun createPick(pick: Pick): Result<String> {
        val firebasePick = pick.toFirebasePick()
        return pickDataSource.createPick(firebasePick, pick.createdBy.uid)
    }

    override suspend fun deletePick(pickId: String, userId: String): Result<String> {
        return pickDataSource.deletePick(pickId, userId)
    }

    override suspend fun fetchPick(pickId: String): Result<Pick> {
        return runCatching {
            val firebasePick = pickDataSource.fetchPick(pickId).getOrThrow()
            firebasePick.toPick()
        }
    }

    override suspend fun fetchMyPicks(userId: String): Result<List<Pick>> {
        return runCatching {
            val firebasePicks = pickDataSource.fetchMyPicks(userId).getOrThrow()
            firebasePicks.map { it.toPick() }
        }
    }

    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): Result<List<Pick>> {
        return runCatching {
            val firebasePicks = pickDataSource.fetchPicksInArea(lat, lng, radiusInM).getOrThrow()
            firebasePicks.map { it.toPick() }
        }
    }

    override suspend fun fetchFavoritePicks(userId: String): Result<List<Pick>> {
        return runCatching {
            val firebasePicks = pickDataSource.fetchFavoritePicks(userId).getOrThrow()
            firebasePicks.map { it.toPick() }
        }
    }

    companion object {
        const val TAG_LOG = "FirebasePickRepositoryImpl"
    }
}
