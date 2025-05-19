package com.squirtles.data.pick

import com.squirtles.core.model.Pick
import com.squirtles.data.firebase.model.FirebasePick
import kotlinx.coroutines.flow.Flow

data class PickWithType(
    val type: PickType,
    val pick: Pick
)

enum class PickType {
    UPDATED, REMOVED
}

interface FirebasePickDataSource {
    suspend fun fetchPick(pickId: String): Result<FirebasePick>
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): Flow<List<PickWithType>>
    suspend fun createPick(firebasePick: FirebasePick, userId: String): Result<String>
    suspend fun deletePick(pickId: String, userId: String): Result<String>
    suspend fun fetchMyPicks(userId: String): Result<List<FirebasePick>>
    suspend fun fetchFavoritePicks(userId: String): Result<List<FirebasePick>>
}
