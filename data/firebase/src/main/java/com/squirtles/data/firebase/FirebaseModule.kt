package com.squirtles.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.squirtles.core.buildconfig.LocalPropertyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // SingletonComponent에 등록
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance(LocalPropertyProvider.firestoreDbId)
    }
}
