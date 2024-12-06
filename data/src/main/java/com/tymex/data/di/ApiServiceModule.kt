package com.tymex.data.di

import com.example.common.di.qualifier.AppBaseUrl
import com.tymex.data.api_service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing API service dependencies
 *
 * This module is responsible for creating and providing the ApiService instance
 * that will be used throughout the application for network calls.
 *
 * Features:
 * - Installed in SingletonComponent for application-wide single instance
 * - Uses Retrofit to create API service implementation
 * - Scoped as Singleton to ensure single instance throughout app lifecycle
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    /**
     * Provides singleton instance of ApiService
     *
     * @param retrofit Retrofit instance injected with @AppBaseUrl qualifier
     * @return ApiService implementation created by Retrofit
     *
     * Usage:
     * ```
     * @Inject
     * lateinit var apiService: ApiService
     * ```
     *
     * Note:
     * - @Singleton ensures only one instance is created
     * - @AppBaseUrl qualifier is used to identify specific Retrofit instance
     * - Returns a Retrofit-generated implementation of ApiService interface
     */
    @Provides
    @Singleton
    fun provideApiService(@AppBaseUrl retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}