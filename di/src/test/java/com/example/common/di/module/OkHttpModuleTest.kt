package com.example.common.di.module

import com.example.common.di.interceptor.AuthInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Test

class OkHttpModuleTest {
    private val module = OkHttpModule

    @Test
    fun `provideLoggingInterceptor returns interceptor with correct log level`() {
        // When
        val interceptor = module.provideLoggingInterceptor()
        // Then
        assertNotNull(interceptor)
        assertEquals(HttpLoggingInterceptor.Level.BODY, interceptor.level)
    }

    @Test
    fun `provideAuthInterceptor returns AuthInterceptor instance`() {
        // When
        val interceptor = module.provideAuthInterceptor()

        // Then
        assertNotNull(interceptor)
        assertTrue(interceptor is AuthInterceptor)
    }

    @Test
    fun `provideOkHttpClient returns client with correct configuration`() {
        // Given
        val authInterceptor = module.provideAuthInterceptor()
        val loggingInterceptor = module.provideLoggingInterceptor()

        // When
        val client = module.provideOkHttpClient(authInterceptor, loggingInterceptor)

        // Then
        assertNotNull(client)
        assertEquals(30, client.connectTimeoutMillis.div(1000))
        assertEquals(30, client.readTimeoutMillis.div(1000))
        
        // Verify interceptors
        val interceptors = client.interceptors
        assertTrue(interceptors.any { it is AuthInterceptor })
        assertTrue(interceptors.any { it is HttpLoggingInterceptor })
    }
} 