package com.squirtles.domain.firebase

import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.User

interface FirebaseRepository {
    suspend fun createGoogleIdUser(uid: String, email: String, userName: String?, userProfileImage: String?): Result<User>
    suspend fun fetchUser(uid: String): Result<User>
    suspend fun updateUserName(uid: String, newUserName: String): Result<Boolean>
    suspend fun deleteUser(uid: String): Result<Boolean>

    suspend fun fetchPick(pickID: String): Result<Pick>
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): Result<List<Pick>>
    suspend fun createPick(pick: Pick): Result<String>
    suspend fun deletePick(pickId: String, uid: String): Result<Boolean>

    suspend fun fetchMyPicks(uid: String): Result<List<Pick>>
    suspend fun fetchFavoritePicks(uid: String): Result<List<Pick>>
    suspend fun fetchIsFavorite(pickId: String, uid: String): Result<Boolean>
    suspend fun createFavorite(pickId: String, uid: String): Result<Boolean>
    suspend fun deleteFavorite(pickId: String, uid: String): Result<Boolean>
}
