package com.squirtles.data.preference.di

import android.content.Context
import com.squirtles.data.preference.PreferenceRepositoryImpl
import com.squirtles.domain.preference.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceDiModule {
    @Provides
    @Singleton
    fun providePreferenceRepository(@ApplicationContext context: Context): PreferenceRepository =
        PreferenceRepositoryImpl(context)
}
