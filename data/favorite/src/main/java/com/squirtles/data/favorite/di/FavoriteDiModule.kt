package com.squirtles.data.favorite.di

import com.squirtles.data.favorite.CloudFunctionHelper
import com.squirtles.data.favorite.FirebaseFavoriteDataSource
import com.squirtles.data.favorite.FirebaseFavoriteDataSourceImpl
import com.squirtles.domain.favorite.FirebaseFavoriteRepository
import com.squirtles.data.favorite.FirebaseFavoriteRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoriteDiModule {

    @Provides
    @Singleton
    fun provideFirebaseFavoriteRepository(firebaseFavoriteDataSource: FirebaseFavoriteDataSource): FirebaseFavoriteRepository =
        FirebaseFavoriteRepositoryImpl(firebaseFavoriteDataSource)

    @Provides
    @Singleton
    fun provideFirebaseFavoriteDataSource(
        db: FirebaseFirestore,
        cloudFunctionHelper: CloudFunctionHelper
    ): FirebaseFavoriteDataSource =
        FirebaseFavoriteDataSourceImpl(db, cloudFunctionHelper)

    @Provides
    @Singleton
    fun provideCloudFunctionHelper(): CloudFunctionHelper = CloudFunctionHelper()
}
