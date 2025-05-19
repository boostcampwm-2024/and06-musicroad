package com.squirtles.data.user

import com.squirtles.data.firebase.model.FirebaseUser

interface FirebaseUserDataSource {
    suspend fun fetchUser(uid: String): Result<FirebaseUser>
    suspend fun createGoogleIdUser(uid: String, newUser: FirebaseUser): Result<FirebaseUser>
    suspend fun updateUserName(uid: String, newUserName: String): Result<Boolean>
    suspend fun deleteUser(uid: String): Result<Void>
}
