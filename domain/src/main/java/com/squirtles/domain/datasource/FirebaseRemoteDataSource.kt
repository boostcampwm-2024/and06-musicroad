package com.squirtles.domain.datasource

import com.squirtles.domain.model.Pick

interface FirebaseRemoteDataSource {
    suspend fun fetchPick(pickID: String): Pick?
    suspend fun fetchPicksInArea(lat: Double, lng: Double, radiusInM: Double): List<Pick>
    suspend fun addPick(pick: Pick): Pick?
    suspend fun deletePick(pick: Pick): Boolean
//    suspend fun updatePick(pick: Pick)
}