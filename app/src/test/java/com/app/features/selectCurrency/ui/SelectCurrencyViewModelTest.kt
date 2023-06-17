package com.app.features.selectCurrency.ui

import app.cash.turbine.testIn
import com.app.features.dashboard.data.model.Currency
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.*

class SelectCurrencyViewModelTest : StringSpec() {

    companion object {
        private val CURRENCIES = listOf(
            Currency(shortName = "USD", "United State Dollar"),
            Currency(shortName = "INR", "Indian Rupee"),
        )
    }

    private val testDispatcher = StandardTestDispatcher()

    init {
        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        "init_initialStateUpdated" {
            // Arrange
            val SUT = createViewModel()

            // Act
            val screenStateTurbine = SUT.screenState.testIn(this)
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertFalse(actualScreenState.isLoading)
            assertEquals(0, actualScreenState.currencies.size)
        }

        "init_stateUpdated_loadingTrue" {
            // Arrange
            val SUT = createViewModel()

            // Act
            val screenStateTurbine = SUT.screenState.testIn(this)
            screenStateTurbine.awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertTrue(actualScreenState.isLoading)
        }

        "init_stateUpdated_currencies" {
            // Arrange
            val SUT = createViewModel()

            // Act
            val screenStateTurbine = SUT.screenState.testIn(this)
            screenStateTurbine.awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            screenStateTurbine.awaitItem()
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertEquals(2, actualScreenState.currencies.size)
        }

        "action_selectCurrency_goBackWithCurrency" {
            // Arrange
            val SUT = createViewModel()
            val turbine = SUT.goBackWithCurrency.testIn(this)
            val expectedCurrency = CURRENCIES.first()

            // Act
            SUT.onAction(SelectCurrencyScreenAction.OnSelectCurrency(expectedCurrency))

            testDispatcher.scheduler.advanceUntilIdle()
            val result = turbine.awaitItem()
            turbine.cancel()

            assertEquals(result.fullName, expectedCurrency.fullName)
            assertEquals(result.shortName, expectedCurrency.shortName)
        }
    }

    private fun createViewModel(): SelectCurrencyViewModel {
        val getCurrenciesUseCase = mockk<GetCurrenciesUseCase>(
            block = {
                coEvery { getCurrencies() } returns CURRENCIES
            }
        )
        return SelectCurrencyViewModel(
            getCurrenciesUseCase = getCurrenciesUseCase
        )
    }

}