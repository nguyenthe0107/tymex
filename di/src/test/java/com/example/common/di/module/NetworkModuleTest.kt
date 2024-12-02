package com.example.common.di.module

import io.mockk.mockk
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Test
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkModuleTest {
    private val module = NetworkModule

    @Test
    fun `provideBaseUrl returns correct URL`() {
        // When
        val baseUrl = module.provideBaseUrl()

        // Then
        assertEquals("https://api.github.com/", baseUrl)
    }

    @Test
    fun `provideMoshiConverter returns MoshiConverterFactory`() {
        // Given
        val moshi = module.provideMoshi()

        // When
        val converter = module.provideMoshiConverter(moshi)

        // Then
        assertNotNull(converter)
        assertTrue(converter is MoshiConverterFactory)
    }

    @Test
    fun `provideRetrofit returns Retrofit instance with correct configuration`() {
        // Given
        val okHttpClient = mockk<OkHttpClient>()
        val moshi = module.provideMoshi()
        val converter = module.provideMoshiConverter(moshi)

        // When
        val retrofit = module.provideRetrofit(okHttpClient, converter)

        // Then
        assertNotNull(retrofit)
        assertEquals(module.provideBaseUrl(), retrofit.baseUrl().toString())
    }
} 