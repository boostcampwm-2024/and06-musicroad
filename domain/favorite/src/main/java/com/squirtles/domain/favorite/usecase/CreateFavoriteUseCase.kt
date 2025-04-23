package com.squirtles.domain.favorite.usecase

import com.squirtles.domain.favorite.FirebaseFavoriteRepository
import javax.inject.Inject

class CreateFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FirebaseFavoriteRepository
) {
    suspend operator fun invoke(pickId: String, uid: String) =
        favoriteRepository.createFavorite(pickId, uid)
}
