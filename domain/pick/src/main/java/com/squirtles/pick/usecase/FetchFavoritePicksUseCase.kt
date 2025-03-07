package com.squirtles.pick.usecase

import com.squirtles.model.Pick
import com.squirtles.pick.FirebasePickRepository
import com.squirtles.picklist.FetchPickListUseCaseInterface
import javax.inject.Inject

class FetchFavoritePicksUseCase @Inject constructor(
    private val pickRepository: FirebasePickRepository
) : FetchPickListUseCaseInterface {
    override suspend operator fun invoke(userId: String): Result<List<Pick>> =
        pickRepository.fetchFavoritePicks(userId)
}
