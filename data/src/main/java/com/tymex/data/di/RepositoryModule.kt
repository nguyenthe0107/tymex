package com.tymex.data.di
import com.example.domain.repository.UserInfoRepository
import com.tymex.data.api_service.ApiService
import com.tymex.data.repositoryImpl.UserInfoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserInfoRepository(apiService: ApiService): UserInfoRepository {
        return UserInfoRepositoryImpl(apiService)
    }
}