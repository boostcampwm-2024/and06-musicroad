package com.squirtles.data.applemusic.di

import com.squirtles.data.applemusic.AppleMusicDataSource
import com.squirtles.data.applemusic.AppleMusicDataSourceImpl
import com.squirtles.domain.applemusic.AppleMusicRepository
import com.squirtles.data.applemusic.AppleMusicRepositoryImpl
import com.squirtles.data.applemusic.api.AppleMusicApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppleMusicDiModule{

    private const val BASE_APPLE_MUSIC_URL = "https://api.music.apple.com/"

    @Provides
    @Singleton
    fun provideAppleMusicApi(
        appleOkHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): AppleMusicApi {
        return Retrofit.Builder()
            .baseUrl(BASE_APPLE_MUSIC_URL)
            .addConverterFactory(converterFactory)
            .client(appleOkHttpClient)
            .build()
            .create(AppleMusicApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppleMusicRepository(appleMusicDataSource: AppleMusicDataSource): AppleMusicRepository =
        AppleMusicRepositoryImpl(appleMusicDataSource)

    @Provides
    @Singleton
    fun provideAppleMusicDataSource(api: AppleMusicApi): AppleMusicDataSource =
        AppleMusicDataSourceImpl(api)
}
