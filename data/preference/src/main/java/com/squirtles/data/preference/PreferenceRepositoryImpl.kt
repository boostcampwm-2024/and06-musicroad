package com.squirtles.data.preference

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.squirtles.data.preference.PreferenceKeys.PLAYER_EFFECT
import com.squirtles.domain.preference.PlayerPreference
import com.squirtles.domain.preference.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    private val context: Context
) : PreferenceRepository {
    private val Context.dataStore by preferencesDataStore(name = PLAYER_PREFERENCE_NAME)
    private val dataStore = context.dataStore

    override suspend fun savePlayerPreference(preference: PlayerPreference): Result<Boolean> {
        return runCatching {
            dataStore.edit { pref ->
                pref[PLAYER_EFFECT] = preference.name
            }
            true
        }.onFailure { e ->
            return Result.failure(e)
        }
    }

    override fun loadPlayerPreference(): Flow<PlayerPreference> {
        val pref = dataStore.data.map { pref ->
            pref[PLAYER_EFFECT] ?: "NONE"
        }.map {
            PlayerPreference.valueOf(it)
        }
        return pref
    }

    companion object {
        private const val PLAYER_PREFERENCE_NAME = "player_preference"
    }
}
