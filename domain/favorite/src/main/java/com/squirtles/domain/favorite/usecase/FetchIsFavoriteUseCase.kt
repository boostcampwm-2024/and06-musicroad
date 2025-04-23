package com.squirtles.domain.favorite.usecase

import com.squirtles.domain.favorite.FirebaseFavoriteRepository
import javax.inject.Inject

class FetchIsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FirebaseFavoriteRepository
) {
    suspend operator fun invoke(pickId: String, userId: String) =
        favoriteRepository.fetchIsFavorite(pickId, userId)
}
