package com.squirtles.domain.user.usecase

import com.squirtles.user.FirebaseUserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    operator fun invoke() = firebaseUserRepository.signOut()
}
