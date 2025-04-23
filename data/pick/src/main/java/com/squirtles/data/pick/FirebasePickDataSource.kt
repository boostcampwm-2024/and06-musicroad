package com.squirtles.data.pick

import com.squirtles.data.firebase.model.FirebasePick

interface FirebasePickDataSource {
    suspend fun fetchPick(pickId: String): Result<FirebasePick>
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): Result<List<FirebasePick>>
    suspend fun createPick(firebasePick: FirebasePick, userId: String): Result<String>
    suspend fun deletePick(pickId: String, userId: String): Result<String>
    suspend fun fetchMyPicks(userId: String): Result<List<FirebasePick>>
    suspend fun fetchFavoritePicks(userId: String): Result<List<FirebasePick>>
}
