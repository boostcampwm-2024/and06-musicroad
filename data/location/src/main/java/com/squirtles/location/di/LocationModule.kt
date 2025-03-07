package com.squirtles.location.di

import com.squirtles.location.LocalLocationRepository
import com.squirtles.location.LocalLocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideLocalLocationRepository(): LocalLocationRepository =
        LocalLocationRepositoryImpl()
}
