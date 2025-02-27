package com.squirtles.user

import com.squirtles.model.User

interface FirebaseUserRepository {
    // user
    suspend fun createGoogleIdUser(userId: String, userName: String?, userProfileImage: String?): Result<User>
    suspend fun fetchUser(userId: String): Result<User>
    suspend fun updateUserName(userId: String, newUserName: String): Result<Boolean>
}
