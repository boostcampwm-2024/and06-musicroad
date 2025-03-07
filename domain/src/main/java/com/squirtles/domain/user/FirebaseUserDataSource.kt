package com.squirtles.domain.user

import com.squirtles.domain.model.User

interface FirebaseUserDataSource {
    suspend fun fetchUser(uid: String): User?
    suspend fun createGoogleIdUser(uid: String, email: String, userName: String?, userProfileImage: String?): User?
    suspend fun updateUserName(uid: String, newUserName: String): Boolean
    suspend fun deleteUser(uid: String): Boolean
}
