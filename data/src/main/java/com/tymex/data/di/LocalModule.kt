package com.tymex.data.di

import com.tymex.data.repositoryImpl.UserPreferencesManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for local storage dependencies
 *
 * This module provides components needed for local data persistence using DataStore
 * and JSON serialization/deserialization with Gson.
 */

// Extension property for DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    /**
     * Provides singleton instance of DataStore
     *
     * @param context Application context injected by Hilt
     * @return DataStore instance for storing preferences
     *
     * Usage:
     * - Stores user preferences
     * - Provides persistent key-value storage
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * Provides singleton instance of Gson
     *
     * @return Gson instance for JSON operations
     *
     * Usage:
     * - Serialization of objects to JSON
     * - Deserialization of JSON to objects
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    /**
     * Provides singleton instance of UserPreferencesManager
     *
     * @param dataStore DataStore instance for preferences storage
     * @param gson Gson instance for JSON operations
     * @return UserPreferencesManager instance
     *
     * Usage:
     * - Manages user preferences
     * - Handles data persistence
     * - Provides type-safe access to stored preferences
     */
    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        dataStore: DataStore<Preferences>,
        gson: Gson
    ): UserPreferencesManager {
        return UserPreferencesManager(dataStore, gson)
    }
}