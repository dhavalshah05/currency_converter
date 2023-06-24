package com.app.di.application

import android.content.Context
import androidx.work.WorkManager
import com.app.services.sync.SyncManager
import com.app.services.sync.SyncManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SyncModule {

    @Provides
    @Singleton
    fun provideSyncManager(@ApplicationContext context: Context): SyncManager {
        return SyncManagerImpl(WorkManager.getInstance(context))
    }

}