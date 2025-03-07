package com.squirtles.pick.usecase

import com.squirtles.model.Pick
import com.squirtles.pick.FirebasePickRepository
import javax.inject.Inject

class FetchPickUseCase @Inject constructor(
    private val pickRepository: FirebasePickRepository
) {
    suspend operator fun invoke(pickId: String): Result<Pick> =
        pickRepository.fetchPick(pickId)

    suspend operator fun invoke(lat: Double, lng: Double, radiusInM: Double): Result<List<Pick>> =
        pickRepository.fetchPicksInArea(lat, lng, radiusInM)
}
