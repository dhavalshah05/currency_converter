package com.app.features.dashboard.data

import com.app.db.currencies.CurrencyEntityQueries
import com.app.db.exchangeRates.ExchangeRateEntityQueries
import com.app.features.dashboard.data.remote.models.ApiExchangeRates
import com.app.services.networking.repositories.OpenExchangeRemoteRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify

@Suppress("LocalVariableName", "PrivatePropertyName")
internal class SyncExchangeRatesUseCaseTest : StringSpec() {

    private lateinit var repository: OpenExchangeRemoteRepository
    private lateinit var currencyEntityQueries: CurrencyEntityQueries
    private lateinit var exchangeRateEntityQueries: ExchangeRateEntityQueries
    private lateinit var SUT: SyncExchangeRatesUseCase

    init {
        beforeEach {
            repository = mockk(relaxed = true, relaxUnitFun = true)
            coEvery { repository.getCurrencies() } returns mapOf("INR" to "Indian Rupee")
            coEvery { repository.getExchangeRatesForUSD() } returns ApiExchangeRates(rates = mapOf("INR" to 80.0))

            currencyEntityQueries = mockk(relaxed = true, relaxUnitFun = true)
            exchangeRateEntityQueries = mockk(relaxed = true, relaxUnitFun = true)

            SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)
        }

        "invoke_callCurrenciesApi" {
            // Arrange
            // Act
            SUT.invoke()

            // Assert
            coVerify(exactly = 1) { repository.getCurrencies() }
        }

        "invoke_deleteCurrenciesFromDb" {
            // Arrange
            // Act
            SUT.invoke()

            // Assert
            verify(exactly = 1) { currencyEntityQueries.deleteAll() }
        }

        "invoke_storeCurrenciesInDb" {
            // Arrange
            // Act
            SUT.invoke()

            // Assert
            verify(exactly = 1) { currencyEntityQueries.createCurrency("INR", "Indian Rupee") }
        }

        "invoke_callExchangeRatesApi" {
            // Arrange
            // Act
            SUT.invoke()

            // Assert
            coVerify(exactly = 1) { repository.getExchangeRatesForUSD() }
        }

        "invoke_deleteExchangeRatesFromDb" {
            // Arrange
            // Act
            SUT.invoke()

            // Assert
            verify(exactly = 1) { exchangeRateEntityQueries.deleteAll() }
        }

        "invoke_storeExchangeRatesInDb" {
            // Arrange
            // Act
            SUT.invoke()

            // Assert
            verify(exactly = 1) { exchangeRateEntityQueries.createExchangeRate("INR", 80.0) }
        }

        "invoke_throwException_whenRepositoryThrowException" {
            // Arrange
            coEvery { repository.getCurrencies() } throws Exception()

            // Act

            // Assert
            shouldThrow<Exception> { SUT.invoke() }
            coVerify(exactly = 1) { repository.getCurrencies() }
            coVerify(exactly = 0) { repository.getExchangeRatesForUSD() }
            verify(exactly = 0) { currencyEntityQueries.createCurrency(any(), any()) }
        }

    }
}