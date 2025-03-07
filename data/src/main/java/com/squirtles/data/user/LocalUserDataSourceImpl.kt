package com.squirtles.data.datasource.local

import android.content.Context
import android.location.Location
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squirtles.domain.user.LocalUserDataSource
import com.squirtles.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserDataSourceImpl @Inject constructor(
    private val context: Context,
) : LocalUserDataSource {
    private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

    private var _currentUser: User? = null
    override val currentUser: User?
        get() = _currentUser

    override fun readUidDataStore(): Flow<String?> {
        val dataStoreKey = stringPreferencesKey(USER_ID_KEY)
        return context.dataStore.data.map { preferences ->
            preferences[dataStoreKey]
        }
    }

    override suspend fun saveUidDataStore(uid: String) {
        val dataStoreKey = stringPreferencesKey(USER_ID_KEY)
        context.dataStore.edit { preferences ->
            preferences[dataStoreKey] = uid
        }
    }

    override suspend fun saveCurrentUser(user: User) {
        _currentUser = user
    }

    override suspend fun clearUser() {
        val dataStoreKey = stringPreferencesKey(USER_ID_KEY)
        context.dataStore.edit { preferences ->
            preferences.remove(dataStoreKey)
        }
        _currentUser = null
    }

    companion object {
        private const val USER_PREFERENCES_NAME = "user_preferences"
        private const val USER_ID_KEY = "user_id"
    }
}
