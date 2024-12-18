package com.tymex.data.di
import com.tymex.data.repositoryImpl.UserPreferencesManager
import com.example.domain.repository.UserInfoRepository
import com.tymex.data.api_service.ApiService
import com.tymex.data.repositoryImpl.UserInfoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for repository layer dependencies
 *
 * This module provides repository implementations that bridge the data layer
 * with the domain layer, following clean architecture principles.
 *
 * Features:
 * - Provides repository implementations
 * - Manages dependencies for repositories
 * - Ensures single instance of repositories
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserInfoRepository(apiService: ApiService,
                                  userPreferencesManager: UserPreferencesManager
    ): UserInfoRepository {
        return UserInfoRepositoryImpl(apiService,userPreferencesManager)
    }
}