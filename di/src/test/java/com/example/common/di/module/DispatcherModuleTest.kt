package com.example.common.di.module

import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Test

class DispatcherModuleTest {
    private val module = DispatcherModule

    @Test
    fun `provideDispatcherProvider returns correct dispatchers`() {
        // When
        val provider = module.provideDispatcherProvider()

        // Then
        assertEquals(Dispatchers.Main, provider.main)
        assertEquals(Dispatchers.IO, provider.io)
        assertEquals(Dispatchers.Default, provider.default)
    }

    @Test
    fun `DefaultDispatcherProvider provides correct dispatchers`() {
        // Given
        val provider = DefaultDispatcherProvider()
        // Then
        assertEquals(Dispatchers.Main, provider.main)
        assertEquals(Dispatchers.IO, provider.io)
        assertEquals(Dispatchers.Default, provider.default)
    }
} 