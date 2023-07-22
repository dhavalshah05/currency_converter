package com.app.features.conversionLogs.data

import com.app.db.conversionLog.ConversionLogEntityQueries
import com.app.features.conversionLogs.data.model.ConversionLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetConversionLogsUseCase(
    private val conversionLogEntityQueries: ConversionLogEntityQueries
) {

    suspend fun getAll(): List<ConversionLog> {
        return withContext(Dispatchers.IO) {
            val conversionLogEntities = conversionLogEntityQueries.getAllConversionLogs().executeAsList()
            conversionLogEntities.map { conversionLogEntity ->
                ConversionLog(
                    id = conversionLogEntity.id,
                    shortName = conversionLogEntity.shortName,
                    amount = conversionLogEntity.amount
                )
            }
        }
    }
}