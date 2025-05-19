package com.squirtles.domain.user.usecase

import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    suspend operator fun invoke(userId: String, newUserName: String) =
        firebaseUserRepository.updateUserName(userId, newUserName)
}
