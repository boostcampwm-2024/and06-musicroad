package com.squirtles.favorite.usecase

import com.squirtles.favorite.FirebaseFavoriteRepository
import com.squirtles.picklist.RemovePickUseCaseInterface
import javax.inject.Inject

class DeleteFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FirebaseFavoriteRepository
) : RemovePickUseCaseInterface {
    override suspend operator fun invoke(pickId: String, userId: String): Result<String> =
        favoriteRepository.deleteFavorite(pickId, userId)
}
