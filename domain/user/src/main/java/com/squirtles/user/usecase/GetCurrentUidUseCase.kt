package com.squirtles.user.usecase

import com.squirtles.user.FirebaseUserRepository
import javax.inject.Inject

class GetCurrentUidUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    operator fun invoke() = firebaseUserRepository.currentUser
}
