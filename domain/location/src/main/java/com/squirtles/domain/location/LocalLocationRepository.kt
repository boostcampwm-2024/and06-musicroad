package com.squirtles.domain.location

import android.location.Location
import kotlinx.coroutines.flow.StateFlow

interface LocalLocationRepository {
    val lastLocation: StateFlow<Location?>

    suspend fun saveCurrentLocation(geoLocation: Location)
}
