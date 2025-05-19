package com.squirtles.domain.pick

import com.squirtles.core.model.Pick
import kotlinx.coroutines.flow.Flow

interface FirebasePickRepository {
    suspend fun createPick(pick: Pick): Result<String>
    suspend fun deletePick(pickId: String, userId: String): Result<String>
    suspend fun fetchPick(pickId: String): Result<Pick>
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): Flow<List<Pick>>
    suspend fun fetchMyPicks(userId: String): Result<List<Pick>>
    suspend fun fetchFavoritePicks(userId: String): Result<List<Pick>>
}
