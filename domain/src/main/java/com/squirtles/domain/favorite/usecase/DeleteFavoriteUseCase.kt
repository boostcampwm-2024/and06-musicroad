package com.squirtles.domain.favorite.usecase

import com.squirtles.domain.favorite.FirebaseFavoriteRepository
import com.squirtles.domain.picklist.RemovePickUseCaseInterface
import javax.inject.Inject

class DeleteFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FirebaseFavoriteRepository
) : RemovePickUseCaseInterface {
    override suspend operator fun invoke(pickId: String, userId: String): Result<String> =
        favoriteRepository.deleteFavorite(pickId, userId)
}
