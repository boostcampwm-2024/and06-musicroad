package com.squirtles.domain.repository

import com.squirtles.domain.model.Pick

interface FirebaseRepository {
    suspend fun fetchPick(pickID: String): Result<Pick>
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): Result<List<Pick>>
    suspend fun addPick(pick: Pick): Result<Pick>
    suspend fun deletePick(pick: Pick): Result<Boolean>
}