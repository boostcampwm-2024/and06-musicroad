package com.squirtles.data.repository

import com.squirtles.domain.firebase.FirebaseRemoteDataSource
import com.squirtles.domain.firebase.FirebaseException
import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.User
import com.squirtles.domain.firebase.FirebaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseRemoteDataSource: FirebaseRemoteDataSource
) : FirebaseRepository {

    override suspend fun createGoogleIdUser(uid: String, email: String, userName: String?, userProfileImage: String?): Result<User> {
        return handleResult(FirebaseException.CreatedUserFailedException()) {
            firebaseRemoteDataSource.createGoogleIdUser(uid, email, userName, userProfileImage)
        }
    }

    override suspend fun fetchUser(uid: String): Result<User> {
        return handleResult(FirebaseException.UserNotFoundException()) {
            firebaseRemoteDataSource.fetchUser(uid)
        }
    }

    override suspend fun updateUserName(uid: String, newUserName: String): Result<Boolean> {
        return handleResult {
            firebaseRemoteDataSource.updateUserName(uid, newUserName)
        }
    }

    override suspend fun deleteUser(uid: String): Result<Boolean> {
        return handleResult(FirebaseException.UserNotFoundException()) {
            firebaseRemoteDataSource.deleteUser(uid)
        }
    }

    override suspend fun fetchPick(pickID: String): Result<Pick> {
        return handleResult(FirebaseException.NoSuchPickException()) {
            firebaseRemoteDataSource.fetchPick(pickID)
        }
    }

    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): Result<List<Pick>> {
        val pickList = firebaseRemoteDataSource.fetchPicksInArea(lat, lng, radiusInM)
        return handleResult(FirebaseException.NoSuchPickInRadiusException()) {
            pickList.ifEmpty { null }
        }
    }

    override suspend fun createPick(pick: Pick): Result<String> {
        return handleResult {
            firebaseRemoteDataSource.createPick(pick)
        }
    }

    override suspend fun deletePick(pickId: String, uid: String): Result<Boolean> {
        return handleResult {
            firebaseRemoteDataSource.deletePick(pickId, uid)
        }
    }

    override suspend fun fetchMyPicks(uid: String): Result<List<Pick>> {
        return handleResult {
            firebaseRemoteDataSource.fetchMyPicks(uid)
        }
    }

    override suspend fun fetchFavoritePicks(uid: String): Result<List<Pick>> {
        return handleResult {
            firebaseRemoteDataSource.fetchFavoritePicks(uid)
        }
    }

    override suspend fun fetchIsFavorite(pickId: String, uid: String): Result<Boolean> {
        return handleResult {
            firebaseRemoteDataSource.fetchIsFavorite(pickId, uid)
        }
    }

    override suspend fun createFavorite(pickId: String, uid: String): Result<Boolean> {
        return handleResult {
            firebaseRemoteDataSource.createFavorite(pickId, uid)
        }
    }

    override suspend fun deleteFavorite(pickId: String, uid: String): Result<Boolean> {
        return handleResult {
            firebaseRemoteDataSource.deleteFavorite(pickId, uid)
        }
    }

    private suspend fun <T> handleResult(
        firebaseRepositoryException: FirebaseException,
        call: suspend () -> T?
    ): Result<T> {
        return runCatching {
            call() ?: throw firebaseRepositoryException
        }
    }

    private suspend fun <T> handleResult(
        call: suspend () -> T
    ): Result<T> {
        return runCatching {
            call()
        }
    }
}
