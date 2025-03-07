package com.squirtles.domain.user

import com.squirtles.domain.model.User
import kotlinx.coroutines.flow.Flow

interface LocalUserDataSource {
    val currentUser: User?

    fun readUidDataStore(): Flow<String?>
    suspend fun saveUidDataStore(uid: String)
    suspend fun saveCurrentUser(user: User)
    suspend fun clearUser()
}
