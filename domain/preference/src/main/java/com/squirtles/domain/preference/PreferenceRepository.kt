package com.squirtles.domain.preference

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    suspend fun savePlayerPreference(preference: PlayerPreference): Result<Boolean>
    fun readPlayerPreference(): Flow<PlayerPreference>
}
