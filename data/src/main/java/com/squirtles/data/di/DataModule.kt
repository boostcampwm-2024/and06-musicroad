package com.squirtles.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.squirtles.data.datasource.remote.FirebaseDataSourceImpl
import com.squirtles.data.datasource.remote.TempRemoteDataSourceImpl
import com.squirtles.data.datasource.remote.api.TempApi
import com.squirtles.data.repository.FirebaseRepositoryImpl
import com.squirtles.data.repository.TempRepositoryImpl
import com.squirtles.domain.datasource.FirebaseRemoteDataSource
import com.squirtles.domain.datasource.TempRemoteDataSource
import com.squirtles.domain.repository.FirebaseRepository
import com.squirtles.domain.repository.TempRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Provides
    @Singleton
    fun provideTempRepository(tempRemoteDataSource: TempRemoteDataSource): TempRepository =
        TempRepositoryImpl(tempRemoteDataSource)

    @Provides
    @Singleton
    fun provideFirebaseRepository(firebaseRemoteDataSource: FirebaseRemoteDataSource): FirebaseRepository =
        FirebaseRepositoryImpl(firebaseRemoteDataSource)

    @Provides
    @Singleton
    fun provideTempRemoteDataSource(tempApi: TempApi): TempRemoteDataSource =
        TempRemoteDataSourceImpl(tempApi)

    @Provides
    @Singleton
    fun provideFirebaseRemoteDataSource(db: FirebaseFirestore): FirebaseRemoteDataSource =
        FirebaseDataSourceImpl(db)
}