package com.squirtles.domain.usecase.favorite

import com.squirtles.domain.firebase.FirebaseRepository
import javax.inject.Inject

class CreateFavoriteUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(pickId: String, uid: String) =
        firebaseRepository.createFavorite(pickId, uid)
}
