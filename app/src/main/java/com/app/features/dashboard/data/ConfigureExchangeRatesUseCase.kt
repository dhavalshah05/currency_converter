package com.app.features.dashboard.data

import android.util.Log
import com.app.features.dashboard.data.model.Currency
import com.app.features.dashboard.data.model.ExchangeRate
import com.app.features.dashboard.data.remote.models.ApiExchangeRates
import com.app.services.networking.ApiEndpoints
import com.app.services.networking.ApiExecutor
import com.app.services.networking.ApiResponse

class ConfigureExchangeRatesUseCase(
    private val apiExecutor: ApiExecutor
) {

    suspend fun invoke() {
        val currenciesResponse = getCurrencies()
        if (currenciesResponse is ApiResponse.Error) {
            Log.d(this.javaClass.simpleName, currenciesResponse.error.message.toString())
            return
        }

        if (currenciesResponse is ApiResponse.Success) {
            val currencies = currenciesResponse.data.keys.map { key ->
                val value = currenciesResponse.data[key]!!
                Currency(shortName = key, fullName = value)
            }
            currencies.forEach { item ->
                //Log.d(this.javaClass.simpleName, item.toString())
            }
        }

        val exchangeRatesResponse = getExchangeRatesForUSD()
        if (exchangeRatesResponse is ApiResponse.Success) {
            val apiExchangeRates = exchangeRatesResponse.data
            val exchangeRates = apiExchangeRates.rates.keys.map { key ->
                val value = apiExchangeRates.rates[key]!!
                ExchangeRate(shortName = key, amount = value)
            }
            exchangeRates.forEach { item ->
                Log.d(this.javaClass.simpleName, item.toString())
            }
        }
    }

    private suspend fun getExchangeRatesForUSD() = apiExecutor.executeGet<ApiExchangeRates>(
        url = ApiEndpoints.LATEST,
        queryParams = {
            append("base", "USD")
        }
    )

    private suspend fun getCurrencies() = apiExecutor.executeGet<Map<String, String>>(
        url = ApiEndpoints.CURRENCIES
    )
}