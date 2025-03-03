package com.squirtles.domain.usecase.user

import com.squirtles.domain.firebase.FirebaseRepository
import com.squirtles.domain.model.User
import javax.inject.Inject

class CreateGoogleIdUserUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(
        uid: String,
        email: String,
        userName: String? = null,
        userProfileImage: String? = null
    ): Result<User> = firebaseRepository.createGoogleIdUser(
        uid,
        email,
        userName,
        userProfileImage
    )
}
