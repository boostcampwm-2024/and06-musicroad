package com.squirtles.domain.firebase

import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.User
import kotlinx.coroutines.flow.Flow

enum class PickType {
    UPDATED, REMOVED
}

interface FirebaseRemoteDataSource {
    suspend fun createGoogleIdUser(userId: String, userName: String?, userProfileImage: String?): User?

    suspend fun fetchUser(userId: String): User?
    suspend fun updateUserName(userId: String, newUserName: String): Boolean

    suspend fun fetchPick(pickID: String): Pick?
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): Flow<List<Pair<PickType, Pick>>>
    suspend fun createPick(pick: Pick): String
    suspend fun deletePick(pickId: String, userId: String): Boolean

    suspend fun fetchMyPicks(userId: String): List<Pick>
    suspend fun fetchFavoritePicks(userId: String): List<Pick>
    suspend fun fetchIsFavorite(pickId: String, userId: String): Boolean
    suspend fun createFavorite(pickId: String, userId: String): Boolean
    suspend fun deleteFavorite(pickId: String, userId: String): Boolean
//    suspend fun updatePick(pick: Pick)
}
