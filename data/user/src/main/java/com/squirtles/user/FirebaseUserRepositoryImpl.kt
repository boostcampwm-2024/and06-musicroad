package com.squirtles.user

import com.squirtles.firebase.FirebaseException
import com.squirtles.firebase.handleResult
import com.squirtles.model.User
import javax.inject.Singleton

@Singleton
class FirebaseUserRepositoryImpl(
    private val userDataSource: FirebaseUserDataSource
) : FirebaseUserRepository {

    override suspend fun createGoogleIdUser(
        userId: String,
        userName: String?,
        userProfileImage: String?
    ): Result<User> {
        return userDataSource.createGoogleIdUser(userId, userName, userProfileImage)
            .onFailure {
                throw FirebaseException.CreatedUserFailedException()
            }
    }

    override suspend fun fetchUser(userId: String): Result<User> {
        return userDataSource.fetchUser(userId)
            .onFailure {
                throw FirebaseException.FetchUserFailedException()
            }
    }

    override suspend fun updateUserName(userId: String, newUserName: String): Result<Boolean> {
        return userDataSource.updateUserName(userId, newUserName)
            .onFailure {
                throw FirebaseException.UpdateUserFailedException()
            }
    }
}
