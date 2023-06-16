package com.app.di.application

import android.content.Context
import com.app.db.MyDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    internal fun provideDriver(@ApplicationContext context: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = MyDatabase.Schema,
            context = context,
            name = "currency_converter.db",
        )
    }

    @Singleton
    @Provides
    internal fun provideDatabase(driver: SqlDriver): MyDatabase {
        return MyDatabase(driver)
    }

}