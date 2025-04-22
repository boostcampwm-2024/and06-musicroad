package com.squirtles.favorite.usecase

import com.squirtles.favorite.FirebaseFavoriteRepository
import com.squirtles.domain.picklist.RemovePickUseCaseInterface
import javax.inject.Inject

class DeleteFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FirebaseFavoriteRepository
) : RemovePickUseCaseInterface {
    override suspend operator fun invoke(pickId: String, uid: String): Result<String> =
        favoriteRepository.deleteFavorite(pickId, uid)
}
