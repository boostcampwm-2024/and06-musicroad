package com.squirtles.data.location.di

import android.content.Context
import com.squirtles.domain.location.LocalLocationRepository
import com.squirtles.data.location.LocalLocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationDiModule {
    @Provides
    @Singleton
    fun provideLocalLocationRepository(@ApplicationContext context: Context): LocalLocationRepository =
        LocalLocationRepositoryImpl(context)
}
