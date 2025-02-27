package com.squirtles.user.usecase

import com.squirtles.user.LocalUserRepository
import javax.inject.Inject

class GetUserIdFromDataStoreUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository
) {
    operator fun invoke() = localUserRepository.readUserIdDataStore()
}
