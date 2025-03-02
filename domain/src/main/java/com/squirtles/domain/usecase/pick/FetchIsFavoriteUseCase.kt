package com.squirtles.domain.usecase.pick

import com.squirtles.domain.firebase.FirebaseRepository
import javax.inject.Inject

class FetchIsFavoriteUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(pickId: String, uid: String) =
        firebaseRepository.fetchIsFavorite(pickId, uid)
}
