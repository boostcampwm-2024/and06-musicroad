package com.squirtles.user.usecase

import com.squirtles.user.LocalUserRepository
import javax.inject.Inject

class ClearUserUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository
) {
    suspend operator fun invoke() = localUserRepository.clearUser()
}
