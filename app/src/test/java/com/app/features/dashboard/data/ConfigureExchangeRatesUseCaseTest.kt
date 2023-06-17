package com.app.features.dashboard.data

import com.app.features.dashboard.data.model.Currency
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class ConfigureExchangeRatesUseCaseTest : StringSpec() {

    companion object {
        private val CURRENCIES = listOf(
            Currency(shortName = "USD", "United State Dollar"),
            Currency(shortName = "INR", "Indian Rupee"),
        )
    }

    private fun getCurrenciesUseCaseWith(currencies: List<Currency>): GetCurrenciesUseCase {
        return mockk {
            coEvery { getCurrencies() } returns currencies
        }
    }

    init {
        // When invoked - if currencies are empty - sync is invoked
        // When invoked - if currencies are not empty - sync is not invoked

        "invoke_currenciesEmpty_syncInvoked" {
            // Arrange
            val getCurrenciesUseCase = getCurrenciesUseCaseWith(emptyList())
            val syncExchangeRatesUseCase = mockk<SyncExchangeRatesUseCase>(
                relaxed = true,
                relaxUnitFun = true
            )
            val SUT = ConfigureExchangeRatesUseCase(
                getCurrenciesUseCase = getCurrenciesUseCase,
                syncExchangeRatesUseCase = syncExchangeRatesUseCase
            )

            // Act
            SUT.invoke()

            // Assert
            coVerify(exactly = 1) { syncExchangeRatesUseCase.invoke() }
        }

        "invoke_currenciesNotEmpty_syncNotInvoked" {
            // Arrange
            val getCurrenciesUseCase = getCurrenciesUseCaseWith(CURRENCIES)
            val syncExchangeRatesUseCase = mockk<SyncExchangeRatesUseCase>(
                relaxed = true,
                relaxUnitFun = true
            )
            val SUT = ConfigureExchangeRatesUseCase(
                getCurrenciesUseCase = getCurrenciesUseCase,
                syncExchangeRatesUseCase = syncExchangeRatesUseCase
            )

            // Act
            SUT.invoke()

            // Assert
            coVerify(exactly = 0) { syncExchangeRatesUseCase.invoke() }
        }

    }

}