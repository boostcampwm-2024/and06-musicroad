package com.squirtles.domain.pick.usecase

import com.squirtles.core.model.Pick
import com.squirtles.domain.pick.FirebasePickRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchPickUseCase @Inject constructor(
    private val pickRepository: FirebasePickRepository
) {
    suspend operator fun invoke(pickId: String): Result<Pick> =
        pickRepository.fetchPick(pickId)

    suspend operator fun invoke(lat: Double, lng: Double, radiusInM: Double): Flow<List<Pick>> =
        pickRepository.fetchPicksInArea(lat, lng, radiusInM)
}
