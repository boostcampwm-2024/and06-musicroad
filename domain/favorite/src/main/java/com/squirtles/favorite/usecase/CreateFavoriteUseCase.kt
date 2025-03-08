package com.squirtles.favorite.usecase

import com.squirtles.favorite.FirebaseFavoriteRepository
import javax.inject.Inject

class CreateFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FirebaseFavoriteRepository
) {
    suspend operator fun invoke(pickId: String, uid: String) =
        favoriteRepository.createFavorite(pickId, uid)
}
