package com.app.features.selectCurrency.data

import com.app.db.currencies.CurrencyEntityQueries
import com.app.features.dashboard.data.model.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCurrenciesUseCase(
    private val currencyEntityQueries: CurrencyEntityQueries
) {

    suspend fun invoke(): List<Currency> {
        return withContext(Dispatchers.IO) {
            val currencyEntities = currencyEntityQueries.getAllCurrencies().executeAsList()
            currencyEntities.map {
                Currency(
                    shortName = it.shortName,
                    fullName = it.fullName
                )
            }
        }
    }
}