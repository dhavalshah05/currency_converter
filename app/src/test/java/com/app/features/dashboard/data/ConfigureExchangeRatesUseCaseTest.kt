package com.app.features.dashboard.data

import com.app.features.dashboard.data.model.Currency
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

@Suppress("PrivatePropertyName")
internal class ConfigureExchangeRatesUseCaseTest : StringSpec() {

    companion object {
        private val CURRENCIES = listOf(
            Currency(shortName = "USD", "United State Dollar"),
            Currency(shortName = "INR", "Indian Rupee"),
        )
    }

    private lateinit var getCurrenciesUseCase: GetCurrenciesUseCase
    private lateinit var syncExchangeRatesUseCase: SyncExchangeRatesUseCase
    private lateinit var SUT: ConfigureExchangeRatesUseCase

    init {
        beforeEach {
            getCurrenciesUseCase = mockk {
                coEvery { getCurrencies() } returns CURRENCIES
            }

            syncExchangeRatesUseCase = mockk(
                relaxed = true,
                relaxUnitFun = true
            )

            SUT = ConfigureExchangeRatesUseCase(
                getCurrenciesUseCase = getCurrenciesUseCase,
                syncExchangeRatesUseCase = syncExchangeRatesUseCase
            )
        }

        "invoke_startSync_whenCurrenciesEmpty" {
            // Arrange
            getCurrenciesUseCase = mockk {
                coEvery { getCurrencies() } returns emptyList()
            }

            SUT = ConfigureExchangeRatesUseCase(
                getCurrenciesUseCase = getCurrenciesUseCase,
                syncExchangeRatesUseCase = syncExchangeRatesUseCase
            )

            // Act
            SUT.invoke()

            // Assert
            coVerify(exactly = 1) { syncExchangeRatesUseCase.invoke() }
        }

        "invoke_skipSync_whenCurrenciesNotEmpty" {
            // Arrange

            // Act
            SUT.invoke()

            // Assert
            coVerify(exactly = 0) { syncExchangeRatesUseCase.invoke() }
        }

    }

}