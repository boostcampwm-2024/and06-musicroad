package com.squirtles.data.pick.di

import com.squirtles.data.pick.FirebasePickDataSource
import com.squirtles.data.pick.FirebasePickDataSourceImpl
import com.squirtles.domain.pick.FirebasePickRepository
import com.squirtles.data.pick.FirebasePickRepositoryImpl
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
