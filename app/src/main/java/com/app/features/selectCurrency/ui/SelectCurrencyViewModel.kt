package com.app.features.selectCurrency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.features.dashboard.data.model.Currency
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SelectCurrencyViewModel(
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(SelectCurrencyScreenState())
    val screenState: StateFlow<SelectCurrencyScreenState> = _screenState.asStateFlow()

    private val _goBackWithCurrency: Channel<Currency> = Channel()
    val goBackWithCurrency: Flow<Currency> = _goBackWithCurrency.receiveAsFlow()

    init {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            val currencies = getCurrenciesUseCase.getCurrencies()
            _screenState.update { it.copy(isLoading = false, currencies = currencies) }
        }
    }

    fun onAction(action: SelectCurrencyScreenAction) {
        when (action) {
            is SelectCurrencyScreenAction.OnSelectCurrency -> {
                viewModelScope.launch {
                    _goBackWithCurrency.send(action.currency)
                }
            }
        }
    }
}