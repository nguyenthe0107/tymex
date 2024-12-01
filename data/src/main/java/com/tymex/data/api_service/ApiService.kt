package com.tymex.data.api_service
import com.tymex.data.model.UserInfoDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/users")
    suspend fun fetchUserList(
        @Query("per_page") perPage:Int,
        @Query("since") since:Int,
    ): Response<List<UserInfoDTO>>

    @GET("/users/{login_username}")
    suspend fun fetchUserDetail(
        @Path("login_username") userName:String,
    ): Response<UserInfoDTO>
}