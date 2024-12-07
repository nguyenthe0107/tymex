package com.example.common.di.interceptor
import okhttp3.Interceptor
import okhttp3.Response

/**
 * If you want test by your github token, please fill in here
 * Interceptor for handling authentication in API requests
 *
 * This interceptor adds authentication headers to all outgoing requests.
 * Currently configured for testing purposes.
 */
class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        /* add for test */
        request.addHeader("Authorization", "Bearer Your_Token")
        return chain.proceed(request.build())
    }
}
