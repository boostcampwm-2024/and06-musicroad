package com.squirtles.data.order.di

import com.squirtles.domain.order.LocalPickListOrderRepository
import com.squirtles.data.order.LocalPickListOrderRepositoryImpl
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
