package com.squirtles.domain.user

import com.squirtles.domain.model.User

interface FirebaseUserDataSource {
    suspend fun fetchUser(uid: String): Result<User>
    suspend fun createGoogleIdUser(
        uid: String,
        email: String,
        userName: String?,
        userProfileImage: String?
    ): Result<User>
    suspend fun updateUserName(uid: String, newUserName: String): Result<Boolean>
    suspend fun deleteUser(uid: String): Result<Void>
}
