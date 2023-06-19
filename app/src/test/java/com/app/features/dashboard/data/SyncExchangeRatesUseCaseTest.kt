package com.app.features.dashboard.data

import com.app.db.currencies.CurrencyEntityQueries
import com.app.db.exchangeRates.ExchangeRateEntityQueries
import com.app.features.dashboard.data.remote.models.ApiExchangeRates
import com.app.services.networking.repositories.OpenExchangeRemoteRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

@Suppress("LocalVariableName")
class SyncExchangeRatesUseCaseTest : StringSpec() {

    private lateinit var repository: OpenExchangeRemoteRepository
    private lateinit var currencyEntityQueries: CurrencyEntityQueries
    private lateinit var exchangeRateEntityQueries: ExchangeRateEntityQueries

    init {
        beforeEach {
            repository = mockk(
                relaxed = true,
                relaxUnitFun = true
            ) {
                coEvery { getCurrencies() } returns mapOf("INR" to "Indian Rupee")
                coEvery { getExchangeRatesForUSD() } returns ApiExchangeRates(
                    rates = mapOf("INR" to 80.0)
                )
            }

            currencyEntityQueries = mockk(
                relaxed = true,
                relaxUnitFun = true
            )

            exchangeRateEntityQueries = mockk(
                relaxed = true,
                relaxUnitFun = true
            )
        }

        "invoke_currenciesApiCalled" {
            // Arrange
            // Act
            val SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)
            SUT.invoke()

            // Assert
            coVerify(exactly = 1) { repository.getCurrencies() }
        }

        "invoke_currenciesStoreInDb" {
            // Arrange
            // Act
            val SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)
            SUT.invoke()

            // Assert
            verify(exactly = 1) { currencyEntityQueries.createCurrency("INR", "Indian Rupee") }
        }

        "invoke_exchangeRatesApiCalled" {
            // Arrange
            // Act
            val SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)
            SUT.invoke()

            // Assert
            coVerify(exactly = 1) { repository.getExchangeRatesForUSD() }
        }

        "invoke_exchangeRatesStoredInDb" {
            // Arrange
            // Act
            val SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)
            SUT.invoke()

            // Assert
            verify(exactly = 1) { exchangeRateEntityQueries.createExchangeRate("INR", 80.0) }
        }

        "invoke_throwException_whenRepositoryThrowException" {
            // Arrange
            repository = mockk(
                relaxed = true,
                relaxUnitFun = true
            ) {
                coEvery { getCurrencies() } throws Exception()
            }

            // Act
            val SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)

            // Assert
            assertThrows<Exception> { SUT.invoke() }
            coVerify(exactly = 1) { repository.getCurrencies() }
            coVerify(exactly = 0) { repository.getExchangeRatesForUSD() }
            verify(exactly = 0) { currencyEntityQueries.createCurrency(any(), any()) }
        }

    }
}