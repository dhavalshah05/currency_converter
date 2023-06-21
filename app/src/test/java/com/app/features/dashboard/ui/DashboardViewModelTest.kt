package com.app.features.dashboard.ui

import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import com.app.features.dashboard.data.ConvertRatesUseCase
import com.app.features.dashboard.data.model.ConvertedRate
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.*

@Suppress("PrivatePropertyName")
class DashboardViewModelTest : StringSpec() {

    companion object {
        private const val AMOUNT = 10.0
        private const val CURRENCY = "INR"

        private val CONVERTED_RATES = listOf(
            ConvertedRate(
                sourceShortName = CURRENCY,
                destinationShortName = "USD",
                destinationAmount = 100.0,
                destinationAmountBase = 20.0
            ),
            ConvertedRate(
                sourceShortName = CURRENCY,
                destinationShortName = "CAD",
                destinationAmount = 50.0,
                destinationAmountBase = 5.0
            )
        )
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var convertRatesUseCase: ConvertRatesUseCase
    private lateinit var SUT: DashboardViewModel

    init {
        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        beforeEach {
            convertRatesUseCase = mockk(
                relaxed = true,
                relaxUnitFun = true,
            ) {
                coEvery { invoke(shortName = CURRENCY, amount = AMOUNT) } returns CONVERTED_RATES
            }

            SUT = DashboardViewModel(
                convertRatesUseCase = convertRatesUseCase
            )
        }

        "init_setInitialScreenState" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                assertEquals(0.0, actualScreenState.amount)
                assertEquals(false, actualScreenState.isLoading)
                assertEquals("USD", actualScreenState.selectedCurrency)
                assertEquals(0, actualScreenState.convertedRates.size)
            }
        }

        "onAction_updateAmountInScreenState_whenAmountChanged" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT))
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                assertEquals(AMOUNT, actualScreenState.amount)
            }
        }

        "onAction_updateCurrencyInScreenState_whenCurrencyChanged" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                assertEquals(CURRENCY, actualScreenState.selectedCurrency)
            }
        }

        "onAction_updateConvertedRatesInScreenState_whenExchangeRatesCalculated" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT))
                screenStateTurbine.awaitItem()

                SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))
                screenStateTurbine.awaitItem()

                SUT.onAction(DashboardScreenAction.CalculateExchangeRates)
                testDispatcher.scheduler.advanceUntilIdle()
                val loadingStartedScreenState = screenStateTurbine.awaitItem()
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                assertTrue(loadingStartedScreenState.isLoading)
                assertEquals(2, actualScreenState.convertedRates.size)
                assertFalse(actualScreenState.isLoading)
            }
        }
    }

}