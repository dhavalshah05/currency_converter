package com.app.features.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.features.dashboard.data.ConvertRatesUseCase
import com.fynd.nitrozen.components.textfield.TextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val convertRatesUseCase: ConvertRatesUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(DashboardScreenState())
    val screenState: StateFlow<DashboardScreenState> = _screenState.asStateFlow()

    private val _navigateToSelectCurrency = Channel<Unit>()
    val navigateToSelectCurrency: Flow<Unit> = _navigateToSelectCurrency.receiveAsFlow()

    private val _errorMessage = Channel<String>()
    val errorMessage: Flow<String> = _errorMessage.receiveAsFlow()

    init {
        _screenState.value = DashboardScreenState()
    }

    fun onAction(action: DashboardScreenAction) {
        when (action) {
            is DashboardScreenAction.OnAmountChange -> {
                val amount = action.amount
                val amountState = if (amount.isBlank())
                    TextFieldState.Error("Enter valid amount")
                else if (amount.toDoubleOrNull() == null)
                    TextFieldState.Error("Enter valid amount")
                else
                    TextFieldState.Idle()

                _screenState.value = screenState.value.copy(
                    amount = action.amount,
                    convertedRates = emptyList(),
                    amountState = amountState
                )
            }
            is DashboardScreenAction.OnCurrencyChange -> {
                _screenState.value = screenState.value.copy(
                    selectedCurrency = action.currency,
                    convertedRates = emptyList()
                )
            }
            DashboardScreenAction.CalculateExchangeRates -> {
                calculateExchangeRates()
            }
            DashboardScreenAction.SelectCurrency -> {
                viewModelScope.launch {
                    _navigateToSelectCurrency.send(Unit)
                }
            }
        }
    }

    private fun calculateExchangeRates() {
        val currency = screenState.value.selectedCurrency
        val amount = screenState.value.amount

        viewModelScope.launch {
            if (amount.toInt() <= 0) {
                _errorMessage.send("Amount should be greater than 0")
                return@launch
            }

            _screenState.value = screenState.value.copy(isLoading = true)
            val convertedRates = convertRatesUseCase.invoke(shortName = currency, amount = amount.toDouble())
            _screenState.value = screenState.value.copy(convertedRates = convertedRates, isLoading = false)
        }
    }
}
