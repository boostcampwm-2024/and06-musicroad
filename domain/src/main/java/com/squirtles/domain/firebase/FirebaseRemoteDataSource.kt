package com.squirtles.domain.firebase

import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.User

interface FirebaseRemoteDataSource {
    suspend fun createGoogleIdUser(uid: String, email: String, userName: String?, userProfileImage: String?): User?
    suspend fun fetchUser(uid: String): User?
    suspend fun updateUserName(uid: String, newUserName: String): Boolean
    suspend fun deleteUser(uid: String): Boolean

    suspend fun fetchPick(pickID: String): Pick?
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): List<Pick>
    suspend fun createPick(pick: Pick): String
    suspend fun deletePick(pickId: String, uid: String): Boolean

    suspend fun fetchMyPicks(uid: String): List<Pick>
    suspend fun fetchFavoritePicks(uid: String): List<Pick>
    suspend fun fetchIsFavorite(pickId: String, uid: String): Boolean
    suspend fun createFavorite(pickId: String, uid: String): Boolean
    suspend fun deleteFavorite(pickId: String, uid: String): Boolean
//    suspend fun updatePick(pick: Pick)
}
