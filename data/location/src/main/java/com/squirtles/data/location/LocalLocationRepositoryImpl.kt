package com.squirtles.data.location

import android.location.Location
import com.squirtles.domain.location.LocalLocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

@Singleton
class LocalLocationRepositoryImpl : LocalLocationRepository {
    private var _currentLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    override val lastLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    override suspend fun saveCurrentLocation(geoLocation: Location) {
        _currentLocation.emit(geoLocation)
    }
}
