package com.squirtles.domain.usecase

import com.squirtles.domain.repository.FirebaseRepository
import javax.inject.Inject

class FetchPickUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(pickId: String) =
        firebaseRepository.fetchPick(pickId)
}