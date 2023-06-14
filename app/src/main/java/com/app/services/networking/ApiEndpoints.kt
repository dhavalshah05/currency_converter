package com.app.services.networking

object ApiEndpoints {
    private const val BASE_URL = "https://openexchangerates.org/api"
    const val CURRENCIES = "$BASE_URL/currencies.json"
    const val LATEST = "$BASE_URL/latest.json"
}