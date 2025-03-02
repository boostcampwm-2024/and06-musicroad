package com.squirtles.data.datasource.local

import android.content.Context
import android.location.Location
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squirtles.domain.local.LocalDataSource
import com.squirtles.domain.model.Order
import com.squirtles.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val context: Context,
) : LocalDataSource {
    private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

    private var _currentUser: User? = null
    override val currentUser: User?
        get() = _currentUser

    private var _currentLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    override val lastLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private var _favoriteListOrder = Order.LATEST
    override val favoriteListOrder get() = _favoriteListOrder

    private var _myListOrder = Order.LATEST
    override val myListOrder get() = _myListOrder

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

    override suspend fun saveCurrentLocation(location: Location) {
        _currentLocation.emit(location)
    }

    override suspend fun saveFavoriteListOrder(order: Order) {
        _favoriteListOrder = order
    }

    override suspend fun saveMyListOrder(order: Order) {
        _myListOrder = order
    }

    companion object {
        private const val USER_PREFERENCES_NAME = "user_preferences"
        private const val USER_ID_KEY = "user_id"
    }
}
