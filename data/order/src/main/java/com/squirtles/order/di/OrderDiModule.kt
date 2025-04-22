package com.squirtles.order.di

import com.squirtles.order.LocalPickListOrderRepository
import com.squirtles.order.LocalPickListOrderRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderDiModule {
    @Provides
    @Singleton
    fun provideLocalPickListOrderRepository(): LocalPickListOrderRepository =
        LocalPickListOrderRepositoryImpl()
}
