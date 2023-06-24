package com.app.features.splash.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.features.dashboard.data.ConfigureExchangeRatesUseCase
import com.app.services.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val configureExchangeRatesUseCase: ConfigureExchangeRatesUseCase,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _navigateToDashboard = Channel<Unit>()
    val navigateToDashboard = _navigateToDashboard.receiveAsFlow()

    init {
        viewModelScope.launch {
            configureExchangeRatesUseCase.invoke()
            syncManager.scheduleSyncForOpenExchangeData()
            _navigateToDashboard.send(Unit)
        }
    }

}