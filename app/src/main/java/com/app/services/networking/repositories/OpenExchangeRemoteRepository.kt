package com.app.services.networking.repositories

import com.app.features.dashboard.data.remote.models.ApiExchangeRates
import com.app.services.networking.ApiEndpoints
import com.app.services.networking.ApiExecutor

class OpenExchangeRemoteRepository(
    private val apiExecutor: ApiExecutor
) {

    suspend fun getExchangeRatesForUSD() = apiExecutor.executeGet<ApiExchangeRates>(
        url = ApiEndpoints.LATEST,
        queryParams = {
            append("base", "USD")
        }
    )

    suspend fun getCurrencies() = apiExecutor.executeGet<Map<String, String>>(
        url = ApiEndpoints.CURRENCIES
    )

}