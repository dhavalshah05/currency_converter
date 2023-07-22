package com.app.features.conversionLogs.data

import com.app.db.conversionLog.ConversionLogEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateConversionLogUseCase(
    private val conversionLogEntityQueries: ConversionLogEntityQueries
) {

    suspend fun create(
        shortName: String,
        amount: Double,
    ) {
        return withContext(Dispatchers.IO) {
            conversionLogEntityQueries.createConversionLog(
                id = null,
                shortName = shortName,
                amount = amount,
            )
        }
    }
}