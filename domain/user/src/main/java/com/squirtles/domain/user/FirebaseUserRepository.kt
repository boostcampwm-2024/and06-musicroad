package com.squirtles.domain.user

import com.squirtles.core.model.User

interface FirebaseUserRepository {
    val currentUser: String?

    // user
    fun signOut()
    suspend fun createGoogleIdUser(uid: String, email: String, userName: String?, userProfileImage: String?): Result<User>
    suspend fun fetchUser(userId: String): Result<User>
    suspend fun updateUserName(userId: String, newUserName: String): Result<Boolean>
    suspend fun deleteUser(uid: String): Result<Void>
    suspend fun updateUserProfileImage(uid: String, imageData: ByteArray): Result<Boolean>
    suspend fun deleteUserProfileImage(uid: String): Result<Boolean>
}
