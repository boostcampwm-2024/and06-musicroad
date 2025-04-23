package com.squirtles.domain.user.usecase

import com.squirtles.core.model.User
import com.squirtles.domain.user.FirebaseUserRepository
import javax.inject.Inject

class CreateGoogleIdUserUseCase @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) {
    suspend operator fun invoke(
        uid: String,
        email: String,
        userName: String? = null,
        userProfileImage: String? = null
    ): Result<User> = firebaseUserRepository.createGoogleIdUser(
        uid,
        email,
        userName,
        userProfileImage
    )
}
