package com.tymex.data.repositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import com.example.domain.model.UserInfoResponse
import com.example.domain.utils.ResultApi
import com.tymex.data.api_service.ApiService
import com.tymex.data.model.UserInfoDTO
import com.tymex.data.model.toUserInfo
import kotlinx.coroutines.flow.flowOf
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException

class UserInfoRepositoryImplTest {

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var userPreferencesManager: UserPreferencesManager

    private lateinit var repository: UserInfoRepositoryImpl

    // Test data
    private val mockUserDto = UserInfoDTO(
        userName = "testUser",
        id = 1,
        avatarUrl = "https://example.com/avatar.jpg",
        htmlUrl = "https://github.com/testUser",
        location = "Test Location",
        followers = 100,
        following = 50,
    )

    private val mockUserList = listOf(mockUserDto)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = UserInfoRepositoryImpl(apiService, userPreferencesManager)
    }

    // Tests for fetchUserList
    @Test
    fun `fetchUserList returns Success when API call is successful`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserList(10, 0)).thenReturn(Response.success(mockUserList))

        // Act
        val result = repository.fetchUserList(10, 0).first()

        // Assert
        assertTrue(result is ResultApi.Success)
        assertEquals(mockUserList.map { it.toUserInfo() }, (result as ResultApi.Success).data)
        verify(userPreferencesManager).saveUserList(mockUserList)
    }

    @Test
    fun `fetchUserList returns Error when API returns empty list`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserList(10, 0)).thenReturn(Response.success(emptyList()))

        // Act
        val result = repository.fetchUserList(10, 0).first()

        // Assert
        assertTrue(result is ResultApi.Error)
        assertEquals("Empty response body", (result as ResultApi.Error).message)
    }

    @Test
    fun `fetchUserList returns cached data when API call fails`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserList(10, 0)).thenThrow(IOException("Network error"))
        `when`(userPreferencesManager.getUserList()).thenReturn(flowOf(mockUserList))

        // Act
        val result = repository.fetchUserList(10, 0).first()

        // Assert
        assertTrue(result is ResultApi.Success)
        assertEquals(mockUserList.map { it.toUserInfo() }, (result as ResultApi.Success).data)
    }

    @Test
    fun `fetchUserList returns Error when API fails and no cache available`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserList(10, 0)).thenThrow(IOException("Network error"))
        `when`(userPreferencesManager.getUserList()).thenReturn(flowOf(null))

        // Act
        val result = repository.fetchUserList(10, 0).first()

        // Assert
        assertTrue(result is ResultApi.Error)
        assertEquals("Unexpected error occurred", (result as ResultApi.Error).message)
    }

    @Test
    fun `fetchUserList emits Loading state before making API call`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserList(10, 0)).thenReturn(Response.success(mockUserList))

        // Act
        val results = mutableListOf<ResultApi<List<UserInfoResponse>>>()
        repository.fetchUserList(10, 0).collect { results.add(it) }

        // Assert
        assertTrue(results[0] is ResultApi.Loading)
        assertTrue(results[1] is ResultApi.Success)
    }

    // Tests for fetchUserDetail
    @Test
    fun `fetchUserDetail returns Success when API call is successful`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserDetail("testUser")).thenReturn(Response.success(mockUserDto))

        // Act
        val result = repository.fetchUserDetail("testUser").first()

        // Assert
        assertTrue(result is ResultApi.Success)
        assertEquals(mockUserDto.toUserInfo(), (result as ResultApi.Success).data)
    }

    @Test
    fun `fetchUserDetail returns Error when API returns null`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserDetail("testUser")).thenReturn(Response.success(null))

        // Act
        val result = repository.fetchUserDetail("testUser").first()

        // Assert
        assertTrue(result is ResultApi.Error)
        assertEquals("Empty response body", (result as ResultApi.Error).message)
    }

    @Test
    fun `fetchUserDetail returns Error when API call fails`() = runBlocking {
        // Arrange
        val errorResponse = Response.error<UserInfoDTO>(
            404,
            mock(ResponseBody::class.java)
        )
        `when`(apiService.fetchUserDetail("testUser")).thenReturn(errorResponse)

        // Act
        val result = repository.fetchUserDetail("testUser").first()

        // Assert
        assertTrue(result is ResultApi.Error)
        assertEquals(404, (result as ResultApi.Error).code)
    }

    @Test
    fun `fetchUserDetail returns Error when exception occurs`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserDetail("testUser")).thenThrow(IOException("Network error"))

        // Act
        val result = repository.fetchUserDetail("testUser").first()

        // Assert
        assertTrue(result is ResultApi.Error)
        assertEquals(-1, (result as ResultApi.Error).code)
        assertTrue(result.cause is IOException)
    }

    @Test
    fun `fetchUserDetail emits Loading state before making API call`() = runBlocking {
        // Arrange
        `when`(apiService.fetchUserDetail("testUser")).thenReturn(Response.success(mockUserDto))

        // Act
        val results = mutableListOf<ResultApi<UserInfoResponse>>()
        repository.fetchUserDetail("testUser").collect { results.add(it) }

        // Assert
        assertTrue(results[0] is ResultApi.Loading)
        assertTrue(results[1] is ResultApi.Success)
    }
}