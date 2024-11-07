package com.squirtles.data.datasource.remote.api

import com.squirtles.data.datasource.remote.model.spotify.SpotifyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface SpotifyApi {
    @GET("v1/search")
    suspend fun searchTracks(
        @Header("Authorization") token: String,
        @QueryMap queryMap: Map<String, String>
    ): Response<SpotifyResponse>
}
