package com.app.features.dashboard.data.model

data class ConvertedRate(
    val sourceShortName: String,
    val destinationShortName: String,
    val destinationAmount: Double,
    val destinationAmountBase: Double
)