package com.tymex.data.repositoryImpl

import com.example.config.Constants.ERR_COMMON
import com.example.domain.model.UserInfoResponse
import com.example.domain.utils.ApiError
import com.example.domain.utils.ResultApi
import com.google.gson.Gson
import kotlinx.coroutines.flow.FlowCollector
import retrofit2.Response

object HelperApi {
    // Helper function to handle API errors and return a common ResultApi.Error
    suspend fun <T> FlowCollector<ResultApi<T>>.handleError(
        response: Response<*>
    ) {
        val errorBodyString = response.errorBody()?.string()
        val error = errorBodyString?.let {
            Gson().fromJson(it, ApiError::class.java)
        }
        emit(
            ResultApi.Error(
                message = "$ERR_COMMON: ${error?.message}",
                code = response.code()
            )
        )
    }

    suspend fun FlowCollector<ResultApi<UserInfoResponse>>.emitCommonErr(
        e: Exception
    ) {
        emit(
            ResultApi.Error(
                "$ERR_COMMON: ${e.message}",
                code = -1,
                cause = e
            )
        )
    }
}