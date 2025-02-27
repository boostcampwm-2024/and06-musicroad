package com.squirtles.user.usecase

import com.squirtles.user.LocalUserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository
) {
    operator fun invoke() = localUserRepository.currentUser
}
