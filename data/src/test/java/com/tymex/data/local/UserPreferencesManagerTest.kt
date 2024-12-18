package com.tymex.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import com.google.gson.Gson
import com.tymex.data.model.UserInfoDTO
import com.tymex.data.repositoryImpl.UserPreferencesManager
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class UserPreferencesManagerTest {
    private lateinit var userPreferencesManager: UserPreferencesManager
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var gson: Gson

    @Before
    fun setup() {
        dataStore = mockk(relaxed = true)
        gson = Gson()
        userPreferencesManager = UserPreferencesManager(dataStore, gson)
    }

    @Test
    fun `saveUserList saves data correctly`() = runTest {
        // Given
        val mockUsers = listOf(createMockUserDTO())
        coEvery { dataStore.updateData(any()) } returns preferencesOf()
        // When
        userPreferencesManager.saveUserList(mockUsers)
        // Then
        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `getUserList returns cached data when not expired`() = runTest {
        // Given
        coEvery { dataStore.data } returns flowOf(preferencesOf())
        // When
        userPreferencesManager.getUserList().collect { result ->
            // Then
            assertTrue(result?.isEmpty() ?: false)
        }
    }

    @Test
    fun `appendUserList combines existing and new data`() = runTest {
        // Given
        val newUsers = listOf(createMockUserDTO(id = 2))
        coEvery { dataStore.data } returns flowOf(preferencesOf())
        // When
        userPreferencesManager.appendUserList(newUsers)
        // Then
        coVerify { dataStore.updateData(any()) }
    }

    private fun createMockUserDTO(id: Int = 1) = UserInfoDTO(
        id = id,
        userName = "test$id",
        avatarUrl = "url$id",
        htmlUrl = "html$id",
        location = null,
        followers = null,
        following = null
    )
} 