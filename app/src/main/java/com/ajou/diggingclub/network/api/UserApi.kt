package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.NicknameErrorBody
import com.ajou.diggingclub.network.models.SignUpRequestBody
import com.ajou.diggingclub.network.models.SignUpResponseBody
import com.ajou.diggingclub.network.models.TokenResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    @Headers("Content-Type:application/json")
    @POST("signup")
    fun signUpUser(@Body userInfo : SignUpRequestBody) : Call<SignUpResponseBody>

    @POST("kakao")
    fun login(@Body authCode: RequestBody) : Call<TokenResponse>

    @POST("nickname-validation")
    fun checkNickname(
        @Header("AccessToken") accessToken : String?,
        @Header("RefreshToken") refreshToken : String?,
        @Body nickname : RequestBody
    ) : Call<ResponseBody>

    @GET("nickname")
    fun getNickname(
        @Header("AccessToken") accessToken : String?,
        @Header("RefreshToken") refreshToken : String
    ) : Call<ResponseBody>

    @POST("nickname")
    fun setNickname(
        @Header("AccessToken") token : String?,
        @Body nickname : RequestBody
    ) : Call<ResponseBody>
}