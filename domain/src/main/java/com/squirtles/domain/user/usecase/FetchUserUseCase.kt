package com.squirtles.domain.user.usecase

import com.squirtles.domain.model.User
import com.squirtles.domain.user.LocalUserRepository
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
    private val fetchUserByIdUseCase: FetchUserByIdUseCase
) {
    suspend operator fun invoke(userId: String): Result<User> {
        val user = fetchUserByIdUseCase(userId) // userId가 있으면 Firestore에서 유저 가져오기
            .onSuccess { user ->
                localUserRepository.saveUserIdDataStore(user.uid)
                localUserRepository.saveCurrentUser(user) // Firestore에서 가져온 user를 LocalDataSource에 저장
            }
        return user
    }
}
