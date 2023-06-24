package com.app.services.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.features.dashboard.data.SyncExchangeRatesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncExchangeRatesUseCase: SyncExchangeRatesUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "Sync process started")
        return try {
            syncExchangeRatesUseCase.invoke()
            Log.d("SyncWorker", "Sync process success")
            Result.success()
        } catch (e: Exception) {
            Log.d("SyncWorker", "Sync process retry")
            Result.retry()
        }
    }


}