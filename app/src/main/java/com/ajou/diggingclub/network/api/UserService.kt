package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @POST("kakao")
    fun login(@Body authCode: RequestBody) : Call<TokenResponse>

    @GET("members")
    suspend fun getUserInfo(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
    ) : Response<ResponseBody>


    @POST("nickname-validation")
    fun checkNickname(
        @Header("AccessToken") accessToken : String?,
        @Header("RefreshToken") refreshToken : String?,
        @Body nickname : RequestBody
    ) : Call<ResponseBody>

    @GET("nickname")
    suspend fun getNickname(
        @Header("AccessToken") accessToken : String?,
        @Header("RefreshToken") refreshToken : String
    ) : Response<ResponseBody>

    @POST("nickname")
    fun setNickname(
        @Header("AccessToken") accessToken : String?,
        @Header("RefreshToken") refreshToken : String?,
        @Body nickname : RequestBody
    ) : Call<ResponseBody>

    @POST("withdrawal")
    fun withdrawal(
        @Header("AccessToken") accessToken : String?,
        @Header("RefreshToken") refreshToken : String?,
        @Body reason : RequestBody
    ) : Call<ResponseBody>

    @POST("logout")
    fun logout(
        @Header("AccessToken") accessToken : String?,
        @Header("RefreshToken") refreshToken : String?
    ) : Call<ResponseBody>
}