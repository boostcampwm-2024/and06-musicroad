package com.squirtles.domain.usecase.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.squirtles.domain.firebase.FirebaseRepository
import com.squirtles.domain.usecase.favorite.DeleteFavoriteUseCase
import com.squirtles.domain.usecase.favorite.FetchFavoritePicksUseCase
import com.squirtles.domain.usecase.mypick.DeletePickUseCase
import com.squirtles.domain.usecase.mypick.FetchMyPicksUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val fetchFavoritePicksUseCase: FetchFavoritePicksUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val fetchMyPicksUseCase: FetchMyPicksUseCase,
    private val deletePickUseCase: DeletePickUseCase,
    private val firebaseRepository: FirebaseRepository
) {
    suspend operator fun invoke() = coroutineScope {
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            try {
                // 1. 좋아한 픽 삭제
                val favoritePicks = fetchFavoritePicksUseCase(currentUser.uid).getOrNull() ?: emptyList()
                val favoritePicksDeleteJobs = favoritePicks.map {
                    async { deleteFavoriteUseCase(it.id, currentUser.uid) }
                }

                // 2. 등록한 픽 삭제
                val myPicks = fetchMyPicksUseCase(currentUser.uid).getOrNull() ?: emptyList()
                val myPicksDeleteJobs = myPicks.map {
                    async { deletePickUseCase(it.id, currentUser.uid) }
                }

                // 모든 삭제 작업이 끝날 때까지 기다림
                (favoritePicksDeleteJobs + myPicksDeleteJobs).awaitAll()

                // 3. Firebase Firestore 유저 정보 삭제
                firebaseRepository.deleteUser(currentUser.uid)

                // 4. Firebase Auth 유저 삭제
                currentUser.delete()
            } catch (e: Exception) {
                Log.e("DeleteAccount", "Error deleting user account: ${e.message}")
            }
        }
    }
}
