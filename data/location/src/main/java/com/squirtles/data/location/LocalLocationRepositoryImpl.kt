package com.squirtles.data.location

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squirtles.core.model.LocationPoint
import com.squirtles.domain.location.LocalLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLocationRepositoryImpl @Inject constructor(
    private val context: Context
) : LocalLocationRepository {
    private val Context.dataStore by preferencesDataStore(name = LOCATION_PREFERENCES_NAME)

    private val latKey = stringPreferencesKey(LAT_KEY)
    private val lngKey = stringPreferencesKey(LNG_KEY)

    override fun readLastLocation(): Flow<LocationPoint?> {
        return context.dataStore.data.map { preferences ->
            val lat = preferences[latKey]?.toDoubleOrNull()
            val lng = preferences[lngKey]?.toDoubleOrNull()

            if (lat != null && lng != null) LocationPoint(lat, lng) else null
        }
    }

    override suspend fun saveLastLocation(location: LocationPoint) {
        context.dataStore.edit { preferences ->
            preferences[latKey] = location.latitude.toString()
            preferences[lngKey] = location.longitude.toString()
        }
    }

    companion object {
        private const val LOCATION_PREFERENCES_NAME = "location_preferences"
        private const val LAT_KEY = "lat_key"
        private const val LNG_KEY = "lng_key"
    }
}
