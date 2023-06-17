package com.app.features.selectCurrency.data

import com.app.db.MyDatabase
import com.app.db.currencies.CurrencyEntity
import com.app.db.currencies.CurrencyEntityQueries
import com.app.features.dashboard.data.model.Currency
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions

class GetCurrenciesUseCaseTest : StringSpec() {
    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    init {
        MyDatabase.Schema.create(driver)

        "invoke_dbFunctionCalled" {
            // Arrange
            val currencyEntityQueries = mockk<CurrencyEntityQueries>(
                relaxed = true,
                relaxUnitFun = true
            )
            val SUT = GetCurrenciesUseCase(currencyEntityQueries)

            // Act
            SUT.invoke()

            // Assert
            verify(exactly = 1) { currencyEntityQueries.getAllCurrencies().executeAsList() }
        }

        "invoke_currenciesReturned" {
            // Arrange
            val currencyEntityQueries = mockk<CurrencyEntityQueries>(
                relaxed = true,
                relaxUnitFun = true
            ) {
                every { getAllCurrencies().executeAsList() } returns listOf(
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
            val SUT = GetCurrenciesUseCase(currencyEntityQueries)

            // Act
            val currencies: List<Currency> = SUT.invoke()

            // Assert
            Assertions.assertEquals(2, currencies.size)
        }

    }
}