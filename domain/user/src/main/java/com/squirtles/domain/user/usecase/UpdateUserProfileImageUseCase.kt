package com.squirtles.domain.user.usecase

import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Inject

class UpdateUserProfileImageUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    suspend operator fun invoke(userId: String, newImageData: ByteArray) {
        firebaseUserRepository.updateUserProfileImage(userId, newImageData)
    }
}
