package com.app.features.dashboard.data

import android.util.Log
import com.app.features.dashboard.data.model.Currency
import com.app.features.dashboard.data.model.ExchangeRate
import com.app.services.networking.repositories.OpenExchangeRemoteRepository

class SyncExchangeRatesUseCase(
    private val openExchangeRemoteRepository: OpenExchangeRemoteRepository
) {

    suspend fun invoke() {
        try {
            val currenciesResponse = openExchangeRemoteRepository.getCurrencies()

            val currencies = currenciesResponse.keys.map { key ->
                val value = currenciesResponse[key]!!
                Currency(shortName = key, fullName = value)
            }
            currencies.forEach { item ->
                //Log.d(this.javaClass.simpleName, item.toString())
            }

            val exchangeRatesResponse = openExchangeRemoteRepository.getExchangeRatesForUSD()
            val exchangeRates = exchangeRatesResponse.rates.keys.map { key ->
                val value = exchangeRatesResponse.rates[key]!!
                ExchangeRate(shortName = key, amount = value)
            }
            exchangeRates.forEach { item ->
                Log.d(this.javaClass.simpleName, item.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}