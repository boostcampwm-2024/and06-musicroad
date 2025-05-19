package com.squirtles.domain.location.usecase

import com.squirtles.domain.location.LocalLocationRepository
import javax.inject.Inject

class GetLastLocationUseCase @Inject constructor(
    private val localLocationRepository: LocalLocationRepository
) {
    operator fun invoke() = localLocationRepository.lastLocation
}
