package com.squirtles.domain.location

import com.squirtles.core.model.LocationPoint
import kotlinx.coroutines.flow.Flow

interface LocalLocationRepository {

    fun readLastLocation(): Flow<LocationPoint?>
    suspend fun saveLastLocation(location: LocationPoint)
}
