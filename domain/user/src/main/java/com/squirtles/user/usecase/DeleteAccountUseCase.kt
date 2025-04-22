package com.squirtles.user.usecase

import com.squirtles.favorite.usecase.DeleteFavoriteUseCase
import com.squirtles.domain.pick.usecase.DeletePickUseCase
import com.squirtles.domain.pick.usecase.FetchFavoritePicksUseCase
import com.squirtles.domain.pick.usecase.FetchMyPicksUseCase
import com.squirtles.user.FirebaseUserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val fetchFavoritePicksUseCase: FetchFavoritePicksUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val fetchMyPicksUseCase: FetchMyPicksUseCase,
    private val deletePickUseCase: DeletePickUseCase,
    private val firebaseUserRepository: FirebaseUserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return runCatching {
            val currentUid = getCurrentUidUseCase()
            requireNotNull(currentUid)
            coroutineScope {
                // 1. 좋아한 픽 삭제
                val favoritePicks = fetchFavoritePicksUseCase(currentUid).getOrNull() ?: emptyList()
                val favoritePicksDeleteJobs = favoritePicks.map { pick ->
                    async { deleteFavoriteUseCase(pick.id, currentUid) }
                }

                // 2. 등록한 픽 삭제
                val myPicks = fetchMyPicksUseCase(currentUid).getOrNull() ?: emptyList()
                val myPicksDeleteJobs = myPicks.map { pick ->
                    async { deletePickUseCase(pick.id, currentUid) }
                }

                // 모든 삭제 작업이 끝날 때까지 기다림
                (favoritePicksDeleteJobs + myPicksDeleteJobs).awaitAll()
            }
            // 3. Firebase Firestore 유저 정보 삭제
            firebaseUserRepository.deleteUser(currentUid)
        }
    }
}
