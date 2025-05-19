package com.squirtles.domain.user.usecase

import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Inject

class GetCurrentUidUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    operator fun invoke() = firebaseUserRepository.currentUser
}
