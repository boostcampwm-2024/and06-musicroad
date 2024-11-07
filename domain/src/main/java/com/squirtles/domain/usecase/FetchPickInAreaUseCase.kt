package com.squirtles.domain.usecase

import com.squirtles.domain.repository.FirebaseRepository
import javax.inject.Inject

class FetchPickInAreaUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double, radiusInM: Double) =
        firebaseRepository.fetchPicksInArea(lat, lng, radiusInM)
}