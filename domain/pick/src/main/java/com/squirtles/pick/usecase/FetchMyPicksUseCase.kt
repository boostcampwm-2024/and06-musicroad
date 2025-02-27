package com.squirtles.pick.usecase

import com.squirtles.pick.FirebasePickRepository
import com.squirtles.picklist.FetchPickListUseCaseInterface
import javax.inject.Inject

class FetchMyPicksUseCase @Inject constructor(
    private val pickRepository: FirebasePickRepository
) : FetchPickListUseCaseInterface {
    override suspend operator fun invoke(userId: String) =
        pickRepository.fetchMyPicks(userId)
}
