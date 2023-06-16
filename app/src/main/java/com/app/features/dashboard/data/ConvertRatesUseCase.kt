package com.app.features.dashboard.data

import com.app.db.exchangeRates.ExchangeRateEntityQueries
import com.app.features.dashboard.data.model.ConvertedRate
import com.app.features.dashboard.data.model.ExchangeRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConvertRatesUseCase(
    private val exchangeRateEntityQueries: ExchangeRateEntityQueries,
) {

    suspend fun invoke(shortName: String, amount: Double): List<ConvertedRate> {
        return withContext(Dispatchers.IO) {
            val exchangeRates = exchangeRateEntityQueries.getAllExchangeRates()
                .executeAsList()
                .map { ExchangeRate(it.shortName, it.amount) }

            val baseExchangeRate = exchangeRates.find { it.shortName == shortName }

            requireNotNull(baseExchangeRate)

            exchangeRates.map {
                val finalAmount = (amount * it.amount) / baseExchangeRate.amount
                val convertedAmount = it.amount / baseExchangeRate.amount
                ConvertedRate(
                    sourceShortName = shortName,
                    destinationShortName = it.shortName,
                    destinationAmount = finalAmount,
                    destinationAmountBase = convertedAmount
                )
            }
        }
    }
}