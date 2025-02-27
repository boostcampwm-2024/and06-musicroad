package com.squirtles.location.usecase

import com.squirtles.location.LocalLocationRepository
import javax.inject.Inject

class GetLastLocationUseCase @Inject constructor(
    private val localLocationRepository: LocalLocationRepository
) {
    operator fun invoke() = localLocationRepository.lastLocation
}
