package com.app.features.selectCurrency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.features.dashboard.data.model.Currency
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCurrencyViewModel @Inject constructor(
    private val getCurrenciesUseCase: GetCurrenciesUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(SelectCurrencyScreenState())
    val screenState: StateFlow<SelectCurrencyScreenState> = _screenState.asStateFlow()

    private val _goBackWithCurrency: Channel<Currency> = Channel()
    val goBackWithCurrency: Flow<Currency> = _goBackWithCurrency.receiveAsFlow()

    private val _goBack: Channel<Unit> = Channel()
    val goBack: Flow<Unit> = _goBack.receiveAsFlow()

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
            SelectCurrencyScreenAction.GoBack -> {
                viewModelScope.launch {
                    _goBack.send(Unit)
                }
            }
            is SelectCurrencyScreenAction.OnChangeSearchText -> {
                _screenState.update { it.copy(searchText = action.searchText.trim()) }
                viewModelScope.launch {
                    if (action.searchText.isEmpty()) {
                        _screenState.update { it.copy(filteredCurrencies = emptyList()) }
                    } else {
                        val filteredCurrencies = screenState.value.currencies
                            .filter { it.fullName.lowercase().contains(action.searchText.lowercase()) }
                        _screenState.update { it.copy(filteredCurrencies = filteredCurrencies) }
                    }
                }
            }
        }
    }
}