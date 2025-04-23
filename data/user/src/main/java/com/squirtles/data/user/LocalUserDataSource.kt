package com.squirtles.data.user

import com.squirtles.core.model.User
import kotlinx.coroutines.flow.Flow

interface LocalUserDataSource {
    val currentUser: User?

    fun readUserIdDataStore(): Flow<String?>
    suspend fun saveUserIdDataStore(userId: String)
    suspend fun saveCurrentUser(user: User)
    suspend fun clearUser()
}
