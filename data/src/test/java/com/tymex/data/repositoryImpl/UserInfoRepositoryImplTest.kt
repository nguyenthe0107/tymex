package com.tymex.data.repositoryImpl
import app.cash.turbine.test
import com.example.domain.utils.ResultApi
import com.tymex.data.api_service.ApiService
import com.tymex.data.model.UserInfoDTO
import com.tymex.data.model.toUserInfo
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import retrofit2.Response
import java.io.IOException
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.tymex.data.repositoryImpl.Constants.EMPTY_DATA
import io.mockk.MockKAnnotations
import io.mockk.MockKException
import io.mockk.mockk

class UserInfoRepositoryImplTest {

    private val apiService: ApiService = mockk()
    private val userPreferencesManager: UserPreferencesManager = mockk()

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

    companion object{
        const val PER_PAGE = 20
        const val PAGE = 1
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        repository = UserInfoRepositoryImpl(apiService, userPreferencesManager)
    }

    @Test
    fun `fetchUserList should emit Success when API call is successful`() = runTest {
        coEvery { apiService.fetchUserList(any(), any()) } returns Response.success(mockUserList)
        coEvery { userPreferencesManager.saveUserList(any()) } just Runs
        try {
            repository.fetchUserList(PER_PAGE, PAGE).test {
                assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
                val result = awaitItem() as ResultApi.Success
                assertThat(result.data).isEqualTo(mockUserList.map { it.toUserInfo() })
                awaitComplete()
            }
        } catch (e: MockKException) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun `fetchUserList returns Error when API returns empty list`() = runTest {
        coEvery { apiService.fetchUserList(PER_PAGE, PAGE) } returns Response.success(emptyList())
        repository.fetchUserList(PER_PAGE, PAGE).test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Error
            assertThat(result.message).isEqualTo(EMPTY_DATA)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserList returns cached data when API call fails`() = runTest {
        coEvery { apiService.fetchUserList(PER_PAGE, PAGE) } throws IOException("Network error")
        coEvery { userPreferencesManager.getUserList() } returns flowOf(mockUserList)
        repository.fetchUserList(PER_PAGE, PAGE).test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Success
            assertThat(result.data).isEqualTo(mockUserList.map { it.toUserInfo() })
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserList returns Error when API fails and no cache available`() = runTest {
        coEvery { apiService.fetchUserList(PER_PAGE, PAGE) } throws IOException("Network error")
        coEvery { userPreferencesManager.getUserList() } returns flowOf(emptyList())
        repository.fetchUserList(PER_PAGE, PAGE).test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Error
            assertThat(result.message).isEqualTo(Constants.ERR_COMMON)
            assertThat(result.code).isEqualTo(-1)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail returns Success when API call is successful`() = runTest {
        coEvery { apiService.fetchUserDetail("testUser") } returns Response.success(mockUserDto)
        repository.fetchUserDetail("testUser").test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Success
            assertThat(result.data).isEqualTo(mockUserDto.toUserInfo())
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail returns Error when API returns null`() = runTest {
        coEvery { apiService.fetchUserDetail("testUser") } returns Response.success(null)
        repository.fetchUserDetail("testUser").test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Error
            assertThat(result.message).isEqualTo(EMPTY_DATA)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail returns Error when API call fails`() = runTest {
        val errorResponse = Response.error<UserInfoDTO>(
            404,
            mockk(relaxed = true)
        )
        coEvery { apiService.fetchUserDetail("testUser") } returns errorResponse
        repository.fetchUserDetail("testUser").test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Error
            assertThat(result.code).isEqualTo(404)
            assertThat(result.message).contains(Constants.ERR_COMMON)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail returns Error when exception occurs`() = runTest {
        coEvery { apiService.fetchUserDetail("testUser") } throws IOException("Network error")
        coEvery { userPreferencesManager.getUserList() } returns flowOf(emptyList())
        repository.fetchUserDetail("testUser").test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Error
            assertThat(result.code).isEqualTo(-1)
            assertThat(result.message).contains(Constants.ERR_COMMON)
            assertThat(result.cause).isInstanceOf(IOException::class.java)
            awaitComplete()
        }
    }

    @Test
    fun `fetchUserDetail emits Loading state before making API call`() = runTest {
        coEvery { apiService.fetchUserDetail("testUser") } returns Response.success(mockUserDto)
        repository.fetchUserDetail("testUser").test {
            assertThat(awaitItem()).isInstanceOf(ResultApi.Loading::class.java)
            val result = awaitItem() as ResultApi.Success
            assertThat(result.data).isEqualTo(mockUserDto.toUserInfo())
            awaitComplete()
        }
    }
}