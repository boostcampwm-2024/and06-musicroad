package com.squirtles.data.datasource.remote.api

import com.squirtles.data.datasource.remote.model.spotify.TempResponse
import retrofit2.http.GET

internal interface TempApi {

    @GET("/temp")
    suspend fun getTemp(): TempResponse
}