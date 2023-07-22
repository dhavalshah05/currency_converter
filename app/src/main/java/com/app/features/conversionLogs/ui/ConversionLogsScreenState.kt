package com.app.features.conversionLogs.ui

import com.app.features.conversionLogs.data.model.ConversionLog

data class ConversionLogsScreenState(
    val isLoading: Boolean = false,
    val conversionLogs: List<ConversionLog> = emptyList(),
)