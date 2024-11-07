package com.squirtles.data.repository

import com.squirtles.domain.datasource.FirebaseRemoteDataSource
import com.squirtles.domain.model.Pick
import com.squirtles.domain.repository.FirebaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseRemoteDataSource: FirebaseRemoteDataSource
) : FirebaseRepository {

    override suspend fun fetchPick(pickID: String): Result<Pick> {
        return handleResult(FirebaseRepositoryException.NoSuchPickException()) {
            firebaseRemoteDataSource.fetchPick(pickID)
        }
    }

    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): Result<List<Pick>> {
        val pickList = firebaseRemoteDataSource.fetchPicksInArea(lat, lng, radiusInM)
        return handleResult(FirebaseRepositoryException.NoSuchPickInRadiusException()) {
            pickList.ifEmpty { null }
        }
    }

    override suspend fun addPick(pick: Pick): Result<Pick> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePick(pick: Pick): Result<Boolean> {
        TODO("Not yet implemented")
    }

    private suspend fun <T> handleResult(
        firebaseRepositoryException: FirebaseRepositoryException,
        call: suspend () -> T?
    ): Result<T> {
        return try {
            val response = call()
            if (response != null) {
                Result.success(response)
            } else {
                Result.failure(firebaseRepositoryException)
            }
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }
}