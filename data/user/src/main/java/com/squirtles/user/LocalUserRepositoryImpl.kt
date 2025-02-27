package com.squirtles.user

import com.squirtles.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserRepositoryImpl @Inject constructor(
    private val userDataSource: LocalUserDataSource
) : LocalUserRepository {
    override val currentUser get() = userDataSource.currentUser

    override fun readUserIdDataStore(): Flow<String?> {
        return userDataSource.readUserIdDataStore()
    }

    override suspend fun saveUserIdDataStore(userId: String) {
        userDataSource.saveUserIdDataStore(userId)
    }

    override suspend fun saveCurrentUser(user: User) {
        userDataSource.saveCurrentUser(user)
    }

    override suspend fun clearUser(): Result<Unit> {
        return try {
            userDataSource.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
