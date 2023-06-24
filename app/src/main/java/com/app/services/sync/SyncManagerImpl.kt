package com.app.services.sync

import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SyncManagerImpl(
    private val workManager: WorkManager
) : SyncManager {
    companion object {
        private const val SYNC_WORK = "SyncWork"
    }

    override fun scheduleSyncForOpenExchangeData() {
        val work = PeriodicWorkRequestBuilder<SyncWorker>(repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
            .build()

        workManager
            .enqueueUniquePeriodicWork(SYNC_WORK, ExistingPeriodicWorkPolicy.UPDATE, work)
    }

}