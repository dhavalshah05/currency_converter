package com.app.features.dashboard.data

import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfigureExchangeRatesUseCase(
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val syncExchangeRatesUseCase: SyncExchangeRatesUseCase
) {

    suspend fun invoke() {
        withContext(Dispatchers.IO) {
            val currencies = getCurrenciesUseCase.getCurrencies()
            if (currencies.isEmpty()) {
                syncExchangeRatesUseCase.invoke()
            }
        }
    }
}