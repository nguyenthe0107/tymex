package com.tymex.data.repositoryImpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tymex.data.model.UserInfoDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Saves a list of users to DataStore
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) {
    companion object {
        // Key for storing user list
        private val USER_LIST = stringPreferencesKey("user_list")
        // Key for last cache timestamp
        private val LAST_CACHED_TIME = longPreferencesKey("last_cached_time")
        // Key for last user ID
        private val LAST_SINCE_ID = intPreferencesKey("last_since_id")
    }

    /**
     * Saves a list of users to DataStore
     * This method:
     * - Converts users list to JSON
     * - Stores the current timestamp
     * - Updates the last seen user ID
     *
     * @param users List of users to be saved
     */
    suspend fun saveUserList(users: List<UserInfoDTO>) {
        dataStore.edit { preferences ->
            val usersJson = gson.toJson(users)
            preferences[USER_LIST] = usersJson
            preferences[LAST_CACHED_TIME] = System.currentTimeMillis()
            users.maxByOrNull { it.id }?.let {
                preferences[LAST_SINCE_ID] = it.id
            }
        }
    }

    /**
     * Appends new users to the existing list
     * This method:
     * - Retrieves current users
     * - Combines with new users
     * - Removes duplicates based on ID
     *
     * @param newUsers List of new users to append
     */
    suspend fun appendUserList(newUsers: List<UserInfoDTO>) {
        val currentUsers = getUserList().first() ?: emptyList()
        val updatedUsers = (currentUsers + newUsers).distinctBy { it.id }
        saveUserList(updatedUsers)
    }

    /**
     * Retrieves the list of users from DataStore
     * Features:
     * - Handles IO exceptions
     * - Implements cache expiration (30 minutes)
     * - Deserializes JSON to user list
     *
     * @return Flow containing list of users or null if cache expired
     */
    fun getUserList(): Flow<List<UserInfoDTO>?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val lastCachedTime = preferences[LAST_CACHED_TIME] ?: 0
                val currentTime = System.currentTimeMillis()
                // Cache expires after 30 minutes
                if (currentTime - lastCachedTime > 30 * 60 * 1000) {
                    emptyList()
                } else {
                    preferences[USER_LIST]?.let { json ->
                        val type = object : TypeToken<List<UserInfoDTO>>() {}.type
                        gson.fromJson(json, type)
                    }
                }
            }
    }

    /**
     * Clears all data from DataStore
     * Use this method to reset all stored preferences
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}