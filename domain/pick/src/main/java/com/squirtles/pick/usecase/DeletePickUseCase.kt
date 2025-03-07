package com.squirtles.pick.usecase

import com.squirtles.pick.FirebasePickRepository
import com.squirtles.picklist.RemovePickUseCaseInterface
import javax.inject.Inject

class DeletePickUseCase @Inject constructor(
    private val pickRepository: FirebasePickRepository
) : RemovePickUseCaseInterface {
    override suspend operator fun invoke(pickId: String, userId: String): Result<String> =
        pickRepository.deletePick(pickId, userId)
}
