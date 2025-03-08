package com.squirtles.user

import com.google.firebase.auth.FirebaseAuth
import com.squirtles.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class FirebaseUserRepositoryImpl(
    private val userDataSource: FirebaseUserDataSource
) : FirebaseUserRepository {

    override val currentUser get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun signOut() = FirebaseAuth.getInstance().signOut()

    override suspend fun createGoogleIdUser(
        uid: String,
        email: String,
        userName: String?,
        userProfileImage: String?
    ): Result<User> {
        return userDataSource.createGoogleIdUser(uid, email, userName, userProfileImage)
    }

    override suspend fun fetchUser(userId: String): Result<User> {
        return userDataSource.fetchUser(userId)
    }

    override suspend fun updateUserName(userId: String, newUserName: String): Result<Boolean> {
        return userDataSource.updateUserName(userId, newUserName)
    }

    override suspend fun deleteUser(uid: String): Result<Void> {
        return userDataSource.deleteUser(uid)
    }
}
