package com.squirtles.favorite.usecase

import com.squirtles.favorite.FirebaseFavoriteRepository
import javax.inject.Inject

class FetchIsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FirebaseFavoriteRepository
) {
    suspend operator fun invoke(pickId: String, userId: String) =
        favoriteRepository.fetchIsFavorite(pickId, userId)
}
