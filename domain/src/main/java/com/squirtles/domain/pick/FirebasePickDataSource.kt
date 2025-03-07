package com.squirtles.domain.pick

import com.squirtles.domain.model.Pick

interface FirebasePickDataSource {
    suspend fun fetchPick(pickID: String): Result<Pick>
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): Result<List<Pick>>
    suspend fun createPick(pick: Pick):  Result<String>
    suspend fun deletePick(pickId: String, userId: String): Result<String>
    suspend fun fetchMyPicks(userId: String): Result<List<Pick>>
    suspend fun fetchFavoritePicks(userId: String): Result<List<Pick>>
}
