package com.app.features.dashboard.data

import com.app.db.currencies.CurrencyEntityQueries
import com.app.db.exchangeRates.ExchangeRateEntityQueries
import com.app.features.dashboard.data.model.Currency
import com.app.features.dashboard.data.model.ExchangeRate
import com.app.services.networking.repositories.OpenExchangeRemoteRepository

class SyncExchangeRatesUseCase(
    private val openExchangeRemoteRepository: OpenExchangeRemoteRepository,
    private val currencyEntityQueries: CurrencyEntityQueries,
    private val exchangeRateEntityQueries: ExchangeRateEntityQueries,
) {

    suspend fun invoke() {
        try {
            val currenciesResponse = openExchangeRemoteRepository.getCurrencies()

            val currencies = currenciesResponse.keys.map { key ->
                val value = currenciesResponse[key]!!
                Currency(shortName = key, fullName = value)
            }
            currencies.forEach { currency ->
                currencyEntityQueries.createCurrency(
                    shortName = currency.shortName,
                    fullName = currency.fullName
                )
            }

            val exchangeRatesResponse = openExchangeRemoteRepository.getExchangeRatesForUSD()
            val exchangeRates = exchangeRatesResponse.rates.keys.map { key ->
                val value = exchangeRatesResponse.rates[key]!!
                ExchangeRate(shortName = key, amount = value)
            }
            exchangeRates.forEach { exchangeRate ->
                exchangeRateEntityQueries.createExchangeRate(
                    shortName = exchangeRate.shortName,
                    amount = exchangeRate.amount
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}