package com.app.features.selectCurrency.ui

import app.cash.turbine.turbineScope
import com.app.features.dashboard.data.model.Currency
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldBeEmpty
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@Suppress("PrivatePropertyName")
internal class SelectCurrencyViewModelTest : StringSpec() {

    companion object {
        private val CURRENCIES = listOf(
            Currency(shortName = "USD", "United State Dollar"),
            Currency(shortName = "INR", "Indian Rupee"),
        )
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCurrenciesUseCase: GetCurrenciesUseCase
    private lateinit var SUT: SelectCurrencyViewModel

    init {
        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        beforeEach {
            getCurrenciesUseCase = mockk()
            coEvery { getCurrenciesUseCase.getCurrencies() } returns CURRENCIES

            SUT = SelectCurrencyViewModel(
                getCurrenciesUseCase = getCurrenciesUseCase
            )
        }

        "viewModel_updateScreenStateWithInitialValues_whenCreateScreenState" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.isLoading.shouldBeFalse()
                actualScreenState.currencies.size.shouldBeEqual(0)
            }
        }

        "viewModel_updateScreenStateWithLoading_whenInit" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                testDispatcher.scheduler.advanceUntilIdle()
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.isLoading.shouldBeTrue()
            }
        }

        "init_updateScreenStateWithCurrencies_whenUsecaseReturnCurrencies" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                testDispatcher.scheduler.advanceUntilIdle()
                screenStateTurbine.awaitItem()
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.currencies.size.shouldBeEqual(2)
            }
        }

        "onAction_updateGoBackState_whenSelectCurrency" {
            turbineScope {
                // Arrange
                val turbine = SUT.goBackWithCurrency.testIn(this)
                val expectedCurrency = CURRENCIES.first()

                // Act
                SUT.onAction(SelectCurrencyScreenAction.OnSelectCurrency(expectedCurrency))

                testDispatcher.scheduler.advanceUntilIdle()
                val result = turbine.awaitItem()
                turbine.cancel()

                expectedCurrency.fullName.shouldBeEqual(result.fullName)
                expectedCurrency.shortName.shouldBeEqual(result.shortName)
            }
        }

        "onAction_goBack_whenBackClicked" {
            turbineScope {
                // Arrange
                val turbine = SUT.goBack.testIn(this)

                // Act
                SUT.onAction(SelectCurrencyScreenAction.GoBack)

                testDispatcher.scheduler.advanceUntilIdle()
                val result = turbine.awaitItem()
                turbine.cancel()

                // Assert
                result.shouldNotBeNull()
            }
        }

        "onAction_updateSearchTextInScreenState_whenChangeSearchText" {
            turbineScope {
                // Arrange
                val turbine = SUT.screenState.testIn(this)

                // Act
                SUT.onAction(SelectCurrencyScreenAction.OnChangeSearchText("Demo"))
                testDispatcher.scheduler.advanceUntilIdle()
                val screenState = turbine.expectMostRecentItem()
                turbine.cancel()

                // Assert
                screenState.searchText.shouldBeEqual("Demo")
            }
        }

        "onAction_shouldNotUpdateSearchTextInScreenState_whenEmptySearchText" {
            turbineScope {
                // Arrange
                val turbine = SUT.screenState.testIn(this)

                // Act
                SUT.onAction(SelectCurrencyScreenAction.OnChangeSearchText("  "))
                testDispatcher.scheduler.advanceUntilIdle()
                val expectedScreenStateWithEmptyString = turbine.expectMostRecentItem()
                turbine.cancel()

                // Assert
                expectedScreenStateWithEmptyString.searchText.shouldBeEmpty()
            }
        }

        "onAction_updateFilteredCurrenciesInScreenState_whenValidSearchText" {
            turbineScope {
                // Arrange
                val turbine = SUT.screenState.testIn(this)

                // Act
                SUT.onAction(SelectCurrencyScreenAction.OnChangeSearchText("Indian"))
                testDispatcher.scheduler.advanceUntilIdle()
                val screenState = turbine.expectMostRecentItem()
                turbine.cancel()

                // Assert
                screenState.filteredCurrencies.size.shouldBeEqual(1)
                val filteredCurrency = screenState.filteredCurrencies.first()
                filteredCurrency.shortName.shouldBeEqual("INR")
            }
        }

        "onAction_clearFilteredCurrenciesInScreenState_whenEmptySearchText" {
            turbineScope {
                // Arrange
                val turbine = SUT.screenState.testIn(this)

                // Act
                SUT.onAction(SelectCurrencyScreenAction.OnChangeSearchText("Indian"))
                testDispatcher.scheduler.advanceUntilIdle()
                val screenStateWithSearchText = turbine.expectMostRecentItem()

                SUT.onAction(SelectCurrencyScreenAction.OnChangeSearchText(""))
                testDispatcher.scheduler.advanceUntilIdle()
                val expectedScreenState = turbine.expectMostRecentItem()
                turbine.cancel()

                // Assert
                screenStateWithSearchText.filteredCurrencies.shouldNotBeEmpty()
                expectedScreenState.filteredCurrencies.shouldBeEmpty()
            }
        }
    }

}