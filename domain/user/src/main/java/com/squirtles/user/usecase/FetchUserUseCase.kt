package com.squirtles.user.usecase

import com.squirtles.model.User
import com.squirtles.user.LocalUserRepository
import javax.inject.Inject

class FetchUserUseCase @Inject constructor(
    private val localUserRepository: LocalUserRepository,
    private val fetchUserByIdUseCase: FetchUserByIdUseCase
) {
    suspend operator fun invoke(userId: String): Result<User> {
        val user = fetchUserByIdUseCase(userId) // userId가 있으면 Firestore에서 유저 가져오기
            .onSuccess { user ->
                localUserRepository.saveUserIdDataStore(user.userId)
                localUserRepository.saveCurrentUser(user) // Firestore에서 가져온 user를 LocalDataSource에 저장
            }
        return user
    }
}
