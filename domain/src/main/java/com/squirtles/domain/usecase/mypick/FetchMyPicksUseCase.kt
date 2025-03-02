package com.squirtles.domain.usecase.mypick

import com.squirtles.domain.firebase.FirebaseRepository
import com.squirtles.domain.usecase.picklist.FetchPickListUseCaseInterface
import javax.inject.Inject

class FetchMyPicksUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : FetchPickListUseCaseInterface {
    override suspend operator fun invoke(uid: String) =
        firebaseRepository.fetchMyPicks(uid)
}
