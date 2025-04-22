package com.squirtles.domain.pick.usecase

import com.squirtles.model.Pick
import com.squirtles.domain.pick.FirebasePickRepository
import javax.inject.Inject

class CreatePickUseCase @Inject constructor(
    private val pickRepository: FirebasePickRepository
) {
    suspend operator fun invoke(pick: Pick): Result<String> = pickRepository.createPick(pick)
}
