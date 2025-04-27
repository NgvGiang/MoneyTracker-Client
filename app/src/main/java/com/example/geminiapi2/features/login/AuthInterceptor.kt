package com.example.geminiapi2.features.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Kiểm tra nếu response code là 401 (Unauthorized)
        if (response.code == 401) {
            CoroutineScope(Dispatchers.IO).launch {
                tokenManager.deleteToken()
            }
        }

        return response
    }
}