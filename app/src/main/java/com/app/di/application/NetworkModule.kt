package com.app.di.application

import com.app.BuildConfig
import com.app.services.networking.ApiExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        engine {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
    }.apply {
        addOpenExchangeInterceptor()
    }

    private fun HttpClient.addOpenExchangeInterceptor() {
        plugin(HttpSend).intercept { request ->
            request.url.parameters.append("app_id", BuildConfig.OPEN_EXCHANGE_API_KEY)
            request.url.parameters.append("prettyprint", "false")
            execute(request)
        }
    }

    @Provides
    @Singleton
    fun provideApiExecutor(client: HttpClient): ApiExecutor = ApiExecutor(
        client = client
    )
}