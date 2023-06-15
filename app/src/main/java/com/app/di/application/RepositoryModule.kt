package com.app.di.application

import com.app.services.networking.ApiExecutor
import com.app.services.networking.repositories.OpenExchangeRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideOpenExchangeRemoteRepository(apiExecutor: ApiExecutor) = OpenExchangeRemoteRepository(
        apiExecutor = apiExecutor
    )
}