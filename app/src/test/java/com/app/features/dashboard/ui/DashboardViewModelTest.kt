package com.app.features.dashboard.ui

import app.cash.turbine.testIn
import com.app.features.dashboard.data.model.ConvertedRate
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.*

class DashboardViewModelTest : StringSpec() {

    companion object {
        private const val AMOUNT = 10.0
        private const val CURRENCY = "INR"
    }

    private val testDispatcher = StandardTestDispatcher()

    init {
        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        "screenState_initViewModel_initialScreenStateReturned" {
            // Arrange
            val SUT = createViewModel()

            // Act
            val screenStateTurbine = SUT.screenState.testIn(this)
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertEquals(0.0, actualScreenState.amount)
            assertEquals(false, actualScreenState.isLoading)
            assertEquals("USD", actualScreenState.selectedCurrency)
            assertEquals(0, actualScreenState.convertedRates.size)
        }

        "onAction_changeAmount_amountUpdated" {
            // Arrange
            val SUT = createViewModel()

            // Act
            val screenStateTurbine = SUT.screenState.testIn(this)
            screenStateTurbine.awaitItem()
            SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT))
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertEquals(AMOUNT, actualScreenState.amount)
        }

        "onAction_changeCurrency_currencyUpdated" {
            // Arrange
            val SUT = createViewModel()

            // Act
            val screenStateTurbine = SUT.screenState.testIn(this)
            screenStateTurbine.awaitItem()
            SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertEquals(CURRENCY, actualScreenState.selectedCurrency)
        }

        "onAction_calculateExchangeRates_convertedRatesUpdated" {
            // Arrange
            val SUT = createViewModel()
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

    private fun createViewModel(): DashboardViewModel {
        return DashboardViewModel(
            convertRatesUseCase = mockk(
                relaxed = true,
                relaxUnitFun = true,
            ) {
                coEvery { invoke(shortName = CURRENCY, amount = AMOUNT) } returns listOf(
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
        )
    }

}