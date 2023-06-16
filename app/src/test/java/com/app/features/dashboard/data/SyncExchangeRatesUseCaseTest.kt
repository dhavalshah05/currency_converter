package com.app.features.dashboard.data

import com.app.db.MyDatabase
import com.app.db.currencies.CurrencyEntityQueries
import com.app.db.exchangeRates.ExchangeRateEntityQueries
import com.app.features.dashboard.data.remote.models.ApiExchangeRates
import com.app.services.networking.repositories.OpenExchangeRemoteRepository
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

@Suppress("LocalVariableName")
class SyncExchangeRatesUseCaseTest : StringSpec({
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    MyDatabase.Schema.create(driver)

    "when invoked repository and db functions are called" {
        val repository = mockk<OpenExchangeRemoteRepository>(
            relaxed = true,
            relaxUnitFun = true
        ) {
            coEvery { getCurrencies() } returns mapOf("INR" to "Indian Rupee")
            coEvery { getExchangeRatesForUSD() } returns ApiExchangeRates(
                rates = mapOf("INR" to 80.0)
            )
        }

        val currencyEntityQueries = mockk<CurrencyEntityQueries>(
            relaxed = true,
            relaxUnitFun = true
        )

        val exchangeRateEntityQueries = mockk<ExchangeRateEntityQueries>(
            relaxed = true,
            relaxUnitFun = true
        )

        val SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)
        SUT.invoke()

        coVerify(exactly = 1) { repository.getCurrencies() }
        verify(exactly = 1) { currencyEntityQueries.createCurrency("INR", "Indian Rupee") }
        coVerify(exactly = 1) { repository.getExchangeRatesForUSD() }
        verify(exactly = 1) { exchangeRateEntityQueries.createExchangeRate("INR", 80.0) }
    }

    "throws exception when repository function throws exception" {
        val repository = mockk<OpenExchangeRemoteRepository>(
            relaxed = true,
            relaxUnitFun = true
        ) {
            coEvery { getCurrencies() } throws Exception()
        }

        val currencyEntityQueries = mockk<CurrencyEntityQueries>(
            relaxed = true,
            relaxUnitFun = true
        )

        val exchangeRateEntityQueries = mockk<ExchangeRateEntityQueries>(
            relaxed = true,
            relaxUnitFun = true
        )

        val SUT = SyncExchangeRatesUseCase(repository, currencyEntityQueries, exchangeRateEntityQueries)

        assertThrows<Exception> { SUT.invoke() }
        coVerify(exactly = 1) { repository.getCurrencies() }
        coVerify(exactly = 0) { repository.getExchangeRatesForUSD() }
        verify(exactly = 0) { currencyEntityQueries.createCurrency(any(), any()) }
    }
})