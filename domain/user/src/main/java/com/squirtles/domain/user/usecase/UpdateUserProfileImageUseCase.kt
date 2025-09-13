package com.squirtles.domain.user.usecase

import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Inject

class UpdateUserProfileImageUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    suspend operator fun invoke(userId: String, newUserProfileImage: String?) {
        // TODO 프로필 이미지 업데이트
    }
}
