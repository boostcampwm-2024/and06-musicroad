package com.squirtles.data.repository

import android.location.Location
import com.squirtles.domain.local.LocalDataSource
import com.squirtles.domain.local.LocalRepository
import com.squirtles.domain.model.Order
import com.squirtles.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
) : LocalRepository {
    override val lastLocation get() = localDataSource.lastLocation
    override val favoriteListOrder get() = localDataSource.favoriteListOrder
    override val myListOrder get() = localDataSource.myListOrder

    override fun readUidDataStore(): Flow<String?> {
        return localDataSource.readUidDataStore()
    }

    override suspend fun clearUser(): Result<Unit> {
        return try {
            localDataSource.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCurrentLocation(location: Location) {
        localDataSource.saveCurrentLocation(location)
    }

    override suspend fun saveFavoriteListOrder(order: Order) {
        localDataSource.saveFavoriteListOrder(order)
    }

    override suspend fun saveMyListOrder(order: Order) {
        localDataSource.saveMyListOrder(order)
    }
}
