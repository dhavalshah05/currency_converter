package com.app.features.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.features.dashboard.data.ConvertRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val convertRatesUseCase: ConvertRatesUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(DashboardScreenState())
    val screenState: StateFlow<DashboardScreenState> = _screenState.asStateFlow()

    init {
        _screenState.value = DashboardScreenState()
    }

    fun onAction(action: DashboardScreenAction) {
        when (action) {
            is DashboardScreenAction.OnAmountChange -> {
                _screenState.value = screenState.value.copy(amount = action.amount)
            }
            is DashboardScreenAction.OnCurrencyChange -> {
                _screenState.value = screenState.value.copy(selectedCurrency = action.currency)
            }
            DashboardScreenAction.CalculateExchangeRates -> {
                calculateExchangeRates()
            }
        }
    }

    private fun calculateExchangeRates() {
        val currency = screenState.value.selectedCurrency
        val amount = screenState.value.amount

        viewModelScope.launch {
            _screenState.value = screenState.value.copy(isLoading = true)
            val convertedRates = convertRatesUseCase.invoke(shortName = currency, amount = amount)
            _screenState.value = screenState.value.copy(convertedRates = convertedRates, isLoading = false)
        }
    }
}