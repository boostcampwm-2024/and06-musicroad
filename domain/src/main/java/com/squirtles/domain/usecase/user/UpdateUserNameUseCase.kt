package com.squirtles.domain.usecase.user

import com.squirtles.domain.firebase.FirebaseRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke(uid: String, newUserName: String) =
        firebaseRepository.updateUserName(uid, newUserName)
}
