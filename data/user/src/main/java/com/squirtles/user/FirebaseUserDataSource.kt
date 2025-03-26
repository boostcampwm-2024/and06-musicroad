package com.squirtles.user

import com.squirtles.firebase.model.FirebaseUser
import com.squirtles.model.User

interface FirebaseUserDataSource {
    suspend fun fetchUser(uid: String): Result<FirebaseUser>
    suspend fun createGoogleIdUser(uid: String, newUser: FirebaseUser): Result<FirebaseUser>
    suspend fun updateUserName(uid: String, newUserName: String): Result<Boolean>
    suspend fun deleteUser(uid: String): Result<Void>
}
