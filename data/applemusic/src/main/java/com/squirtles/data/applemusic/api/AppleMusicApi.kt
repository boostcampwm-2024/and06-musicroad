package com.squirtles.data.applemusic.api

import com.squirtles.data.applemusic.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AppleMusicApi {
    @GET("v1/catalog/{storefront}/search")
    suspend fun searchSongs(
        @Path("storefront") storefront: String,
        @Query("types") types: String,
        @Query("term") term: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: String
    ): Response<SearchResponse>
}
