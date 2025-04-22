package com.squirtles.pick.di

import com.squirtles.pick.FirebasePickDataSource
import com.squirtles.pick.FirebasePickDataSourceImpl
import com.squirtles.domain.pick.FirebasePickRepository
import com.squirtles.pick.FirebasePickRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PickDiModule {

    @Provides
    @Singleton
    fun provideFirebasePickRepository(firebasePickDataSource: FirebasePickDataSource): FirebasePickRepository =
        FirebasePickRepositoryImpl(firebasePickDataSource)

    @Provides
    @Singleton
    fun provideFirebasePickDataSource(db: FirebaseFirestore): FirebasePickDataSource =
        FirebasePickDataSourceImpl(db)
}
