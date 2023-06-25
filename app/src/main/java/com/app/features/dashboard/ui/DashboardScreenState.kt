package com.app.features.dashboard.ui

import com.app.features.dashboard.data.model.ConvertedRate
import com.fynd.nitrozen.components.textfield.TextFieldState

data class DashboardScreenState(
    val amount: String = "0",
    val amountState: TextFieldState = TextFieldState.Idle(),
    val selectedCurrency: String = "USD",
    val convertedRates: List<ConvertedRate> = emptyList(),
    val isLoading: Boolean = false,
) {
    val shouldEnableConvertButton = amountState !is TextFieldState.Error
}
