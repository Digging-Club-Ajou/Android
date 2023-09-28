package com.ajou.diggingclub

import com.kakao.sdk.common.Constants.AUTHORIZATION
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token : String = runBlocking {
            tokenManager.getAccessToken().first()
        } ?: return  errorResponse(chain.request())

        val request = chain.request().newBuilder().header(AUTHORIZATION, "Bearer $token").build()

        return chain.proceed(request)
    }
    private fun errorResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_2)
//        .code(NETWORK_ERROR)
        .message("")
        .body(ResponseBody.create(null,""))
        .build()


}