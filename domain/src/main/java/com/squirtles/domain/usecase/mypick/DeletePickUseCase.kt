package com.squirtles.domain.usecase.mypick

import com.squirtles.domain.firebase.FirebaseRepository
import com.squirtles.domain.usecase.picklist.DeletePickListUseCaseInterface
import javax.inject.Inject

class DeletePickUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : DeletePickListUseCaseInterface {
    override suspend operator fun invoke(pickId: String, uid: String): Result<Boolean> =
        firebaseRepository.deletePick(pickId, uid)
}
