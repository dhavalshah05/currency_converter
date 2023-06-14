package com.app.di.activity

import com.app.features.dashboard.data.ConfigureExchangeRatesUseCase
import com.app.services.networking.ApiExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class UseCaseModule {

    @Provides
    fun provideConfigureExchangeRatesUseCase(
        apiExecutor: ApiExecutor
    ) = ConfigureExchangeRatesUseCase(
        apiExecutor = apiExecutor
    )
}