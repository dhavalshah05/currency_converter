package com.app.di.application

import com.app.db.MyDatabase
import com.app.features.dashboard.data.ConfigureExchangeRatesUseCase
import com.app.features.dashboard.data.ConvertRatesUseCase
import com.app.features.dashboard.data.SyncExchangeRatesUseCase
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import com.app.services.networking.repositories.OpenExchangeRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    fun provideSyncExchangeRatesUseCase(
        openExchangeRemoteRepository: OpenExchangeRemoteRepository,
        myDatabase: MyDatabase
    ) = SyncExchangeRatesUseCase(
        openExchangeRemoteRepository = openExchangeRemoteRepository,
        currencyEntityQueries = myDatabase.currencyEntityQueries,
        exchangeRateEntityQueries = myDatabase.exchangeRateEntityQueries,
    )

    @Provides
    fun getCurrenciesUseCase(
        myDatabase: MyDatabase
    ): GetCurrenciesUseCase {
        return GetCurrenciesUseCase(
            currencyEntityQueries = myDatabase.currencyEntityQueries
        )
    }

    @Provides
    fun convertRatesUseCase(
        myDatabase: MyDatabase
    ): ConvertRatesUseCase {
        return ConvertRatesUseCase(
            exchangeRateEntityQueries = myDatabase.exchangeRateEntityQueries
        )
    }

    @Provides
    fun configureExchangeRatesUseCase(
        getCurrenciesUseCase: GetCurrenciesUseCase,
        syncExchangeRatesUseCase: SyncExchangeRatesUseCase
    ): ConfigureExchangeRatesUseCase {
        return ConfigureExchangeRatesUseCase(
            getCurrenciesUseCase = getCurrenciesUseCase,
            syncExchangeRatesUseCase = syncExchangeRatesUseCase,
        )
    }
}