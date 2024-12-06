package com.example.common.di.module
import com.example.common.di.qualifier.AppBaseUrl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @AppBaseUrl
    fun provideBaseUrl(): String {
        return "https://api.github.com/"
    }

    /**
     * Provides a configured Retrofit instance for making API calls
     *
     * @param okHttpClient The configured OkHttpClient for network requests
     * @param factory MoshiConverterFactory for JSON serialization/deserialization
     * @return Configured Retrofit instance
     */
    @Provides
    @AppBaseUrl
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        factory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(provideBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(factory)
            .build()
    }

    /**
     * Provides a Moshi instance for JSON parsing
     *
     * Features:
     * - Singleton instance for app-wide use
     * - Includes Kotlin support through KotlinJsonAdapterFactory
     *
     * @return Configured Moshi instance
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Provides a MoshiConverterFactory for Retrofit
     *
     * Features:
     * - Singleton instance
     * - Uses provided Moshi instance for consistent JSON handling
     *
     * @param moshi The configured Moshi instance
     * @return MoshiConverterFactory for Retrofit
     */
    @Provides
    @Singleton
    fun provideMoshiConverter(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }
}
