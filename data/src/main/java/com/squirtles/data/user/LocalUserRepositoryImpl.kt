package com.squirtles.data.user

import com.squirtles.domain.user.LocalUserDataSource
import com.squirtles.domain.model.User
import com.squirtles.domain.user.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserRepositoryImpl @Inject constructor(
    private val userDataSource: LocalUserDataSource
) : LocalUserRepository {
    override val currentUser get() = userDataSource.currentUser

    override fun readUserIdDataStore(): Flow<String?> {
        return userDataSource.readUidDataStore()
    }

    override suspend fun saveUserIdDataStore(userId: String) {
        userDataSource.saveUidDataStore(userId)
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
