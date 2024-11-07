package com.squirtles.data.datasource.remote

import com.squirtles.data.datasource.remote.api.TempApi
import com.squirtles.data.mapper.toTemp
import com.squirtles.domain.datasource.TempRemoteDataSource
import com.squirtles.domain.model.Temp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TempRemoteDataSourceImpl @Inject constructor(
    private val tempApi: TempApi
) : TempRemoteDataSource {
    override suspend fun getTemp(): Temp {
        return tempApi.getTemp().toTemp()
    }
}