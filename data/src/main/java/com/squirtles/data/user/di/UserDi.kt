package com.squirtles.data.user.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.squirtles.data.datasource.local.LocalUserDataSourceImpl
import com.squirtles.data.user.FirebaseUserDataSourceImpl
import com.squirtles.data.user.FirebaseUserRepositoryImpl
import com.squirtles.data.user.LocalUserRepositoryImpl
import com.squirtles.domain.user.FirebaseUserDataSource
import com.squirtles.domain.user.FirebaseUserRepository
import com.squirtles.domain.user.LocalUserDataSource
import com.squirtles.domain.user.LocalUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserDi {
    @Provides
    @Singleton
    fun provideLocalUserRepository(localUserDataSource: LocalUserDataSource): LocalUserRepository =
        LocalUserRepositoryImpl(localUserDataSource)

    @Provides
    @Singleton
    fun provideLocalUserDataSource(@ApplicationContext context: Context): LocalUserDataSource =
        LocalUserDataSourceImpl(context)

    @Provides
    @Singleton
    fun provideFirebaseUserRepository(firebaseUserDataSource: FirebaseUserDataSource): FirebaseUserRepository =
        FirebaseUserRepositoryImpl(firebaseUserDataSource)

    @Provides
    @Singleton
    fun provideFirebaseUserDataSource(db: FirebaseFirestore): FirebaseUserDataSource =
        FirebaseUserDataSourceImpl(db)
}
