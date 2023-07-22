package com.app.features.selectCurrency.data

import com.app.db.currencies.CurrencyEntity
import com.app.db.currencies.CurrencyEntityQueries
import com.app.features.dashboard.data.model.Currency
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@Suppress("PrivatePropertyName")
internal class GetCurrenciesUseCaseTest : StringSpec() {

    companion object {
        private val CURRENCIES = listOf(
            CurrencyEntity(
                shortName = "INR",
                fullName = "Indian Rupee"
            ),
            CurrencyEntity(
                shortName = "USD",
                fullName = "United State Dollars"
            ),
        )
    }

    private lateinit var currencyEntityQueries: CurrencyEntityQueries
    private lateinit var SUT: GetCurrenciesUseCase

    init {
        beforeEach {
            currencyEntityQueries = mockk(relaxed = true, relaxUnitFun = true)
            SUT = GetCurrenciesUseCase(currencyEntityQueries)
        }

        "invoke_getCurrenciesFromDb" {
            // Arrange
            // Act
            SUT.getCurrencies()

            // Assert
            verify(exactly = 1) { currencyEntityQueries.getAllCurrencies().executeAsList() }
        }

        "invoke_returnCurrencies_whenCurrenciesAreStoredInDb" {
            // Arrange
            every { currencyEntityQueries.getAllCurrencies().executeAsList() } returns CURRENCIES

            // Act
            val currencies: List<Currency> = SUT.getCurrencies()

            // Assert
            currencies.size.shouldBeEqual(2)
        }

    }
}