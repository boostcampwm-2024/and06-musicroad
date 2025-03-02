package com.squirtles.domain.usecase.favorite

import com.squirtles.domain.firebase.FirebaseRepository
import com.squirtles.domain.usecase.picklist.DeletePickListUseCaseInterface
import javax.inject.Inject

class DeleteFavoriteUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : DeletePickListUseCaseInterface {
    override suspend operator fun invoke(pickId: String, uid: String) =
        firebaseRepository.deleteFavorite(pickId, uid)
}
