package com.app.di.activity

import com.app.features.dashboard.data.SyncExchangeRatesUseCase
import com.app.services.networking.repositories.OpenExchangeRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class UseCaseModule {

    @Provides
    fun provideSyncExchangeRatesUseCase(
        openExchangeRemoteRepository: OpenExchangeRemoteRepository
    ) = SyncExchangeRatesUseCase(
        openExchangeRemoteRepository = openExchangeRemoteRepository
    )
}