package com.squirtles.domain.user.usecase

import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Inject

class FetchUserByIdUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    suspend operator fun invoke(userId: String) =
        firebaseUserRepository.fetchUser(userId)
}
