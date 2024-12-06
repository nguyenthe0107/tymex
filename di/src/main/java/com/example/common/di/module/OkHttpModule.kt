package com.example.common.di.module
import com.example.common.di.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OkHttpModule {

    /**
     * Provides a logging interceptor for HTTP request/response logging
     *
     * Features:
     * - Logs HTTP headers and body
     * - Useful for debugging API calls
     * - Singleton instance for consistent logging
     *
     * @return Configured HttpLoggingInterceptor
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return loggingInterceptor
    }

    /**
     * Provides an authentication interceptor
     *
     * Features:
     * - Handles API authentication
     * - Adds authentication headers to requests
     * - Singleton instance for consistent auth handling
     *
     * @return Configured AuthInterceptor
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor()
    }

    /**
     * Provides configured OkHttpClient instance
     *
     * Features:
     * - Adds authentication handling
     * - Includes logging for debugging
     * - Configures timeout settings
     * - Singleton instance for app-wide use
     *
     * @param authInterceptor Interceptor for handling authentication
     * @param loggingInterceptor Interceptor for logging
     * @return Configured OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

}