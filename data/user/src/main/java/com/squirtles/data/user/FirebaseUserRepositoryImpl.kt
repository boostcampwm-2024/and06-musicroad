package com.squirtles.data.user

import com.google.firebase.auth.FirebaseAuth
import com.squirtles.data.firebase.model.FirebaseUser
import com.squirtles.data.firebase.model.toUser
import com.squirtles.core.model.User
import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepositoryImpl @Inject constructor(
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
        return runCatching {
            val newUser = FirebaseUser(email = email, name = userName, profileImage = userProfileImage)
            val firebaseUser = userDataSource.createGoogleIdUser(uid, newUser).getOrThrow()
            firebaseUser.toUser().copy(uid = uid)
        }
    }

    override suspend fun fetchUser(userId: String): Result<User> {
        return runCatching{
            val firebaseUser = userDataSource.fetchUser(userId).getOrThrow()
            firebaseUser.toUser().copy(uid = userId)
        }
    }

    override suspend fun updateUserName(userId: String, newUserName: String): Result<Boolean> {
        return userDataSource.updateUserName(userId, newUserName)
    }

    override suspend fun deleteUser(uid: String): Result<Void> {
        return userDataSource.deleteUser(uid)
    }
}
