package com.squirtles.user.usecase

import com.squirtles.model.User
import com.squirtles.user.FirebaseUserRepository
import com.squirtles.user.LocalUserRepository
import javax.inject.Inject

class CreateGoogleIdUserUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
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
