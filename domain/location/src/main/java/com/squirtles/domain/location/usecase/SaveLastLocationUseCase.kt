package com.squirtles.domain.location.usecase

import android.location.Location
import com.squirtles.domain.location.LocalLocationRepository
import javax.inject.Inject

class SaveLastLocationUseCase @Inject constructor(
    private val localLocationRepository: LocalLocationRepository
) {
    suspend operator fun invoke(location: Location) = localLocationRepository.saveCurrentLocation(location)
}
