package com.squirtles.data.user

import com.squirtles.domain.firebase.FirebaseException
import com.squirtles.domain.model.User
import com.squirtles.domain.user.FirebaseUserDataSource
import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Singleton

@Singleton
class FirebaseUserRepositoryImpl(
    private val userDataSource: FirebaseUserDataSource
) : FirebaseUserRepository {

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
