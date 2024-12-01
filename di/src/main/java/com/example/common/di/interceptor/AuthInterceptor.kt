package com.example.common.di.interceptor
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.addHeader("Authorization", "Bearer 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")
        return chain.proceed(request.build())
    }
}
