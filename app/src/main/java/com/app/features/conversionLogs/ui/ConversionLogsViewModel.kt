package com.app.features.conversionLogs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.features.conversionLogs.data.GetConversionLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversionLogsViewModel @Inject constructor(
    private val getConversionLogsUseCase: GetConversionLogsUseCase
) : ViewModel() {

    private val _goBack = Channel<Unit>()
    val goBack = _goBack.receiveAsFlow()

    private val _screenState = MutableStateFlow(ConversionLogsScreenState())
    val screenState = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            getConversionLogs()
        }
    }

    fun onAction(actions: ConversionLogsActions) {
        when (actions) {
            ConversionLogsActions.GoBack -> {
                viewModelScope.launch {
                    _goBack.send(Unit)
                }
            }
        }
    }

    private suspend fun getConversionLogs() {
        _screenState.update { it.copy(isLoading = true) }
        val conversionLogs = getConversionLogsUseCase.getAll()
        _screenState.update { it.copy(isLoading = false, conversionLogs = conversionLogs) }
    }

}