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

@Singleton
class UserPreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) {
    companion object {
        private val USER_LIST = stringPreferencesKey("user_list")
        private val CURRENT_USER_ID = intPreferencesKey("current_user_id")
        private val LAST_CACHED_TIME = longPreferencesKey("last_cached_time")
        private val LAST_SINCE_ID = intPreferencesKey("last_since_id")
    }

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

    suspend fun appendUserList(newUsers: List<UserInfoDTO>) {
        val currentUsers = getUserList().first() ?: emptyList()
        val updatedUsers = (currentUsers + newUsers).distinctBy { it.id }
        saveUserList(updatedUsers)
    }

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
                    null
                } else {
                    preferences[USER_LIST]?.let { json ->
                        val type = object : TypeToken<List<UserInfoDTO>>() {}.type
                        gson.fromJson(json, type)
                    }
                }
            }
    }

    fun getLastSinceId(): Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[LAST_SINCE_ID] ?: 0
            }
    }

    suspend fun saveCurrentUserDetail(user: UserInfoDTO) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = user.id
            // Update user in the list if exists
            val currentList = getUserList().first() ?: emptyList()
            val updatedList = currentList.map {
                if (it.id == user.id) user else it
            }
            saveUserList(updatedList)
        }
    }

    fun getCurrentUserDetail(): Flow<UserInfoDTO?> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val userId = preferences[CURRENT_USER_ID] ?: return@map null
                val userList = getUserList().first() ?: return@map null
                userList.find { it.id == userId }
            }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}