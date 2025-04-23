package com.squirtles.data.location.di

import com.squirtles.domain.location.LocalLocationRepository
import com.squirtles.data.location.LocalLocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationDiModule {
    @Provides
    @Singleton
    fun provideLocalLocationRepository(): LocalLocationRepository =
        LocalLocationRepositoryImpl()
}
