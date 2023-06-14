package com.app.features.dashboard.data.remote.models

@kotlinx.serialization.Serializable
data class ApiExchangeRates(
    val rates: Map<String, Double>
)