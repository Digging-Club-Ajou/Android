package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.FollowingResponseBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FollowingService {
    @POST("following")
    fun postFollowing(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Body following : RequestBody
    ) : Call<ResponseBody>

    @HTTP(method = "DELETE", path = "following", hasBody = true)
    fun deleteFollowing(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Body following : RequestBody
    ) : Call<ResponseBody>

    @GET("following-validation")
    suspend fun getFollowingStatus(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Query("memberId") memberId : String
    ) : Response<ResponseBody>

    @GET("followings")
    suspend fun getMyFollowingList(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
    ) : Response<FollowingResponseBody>

    @GET("followings/{memberId}")
    suspend fun getFollowingList(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path("memberId") memberId : String
    ) : Response<FollowingResponseBody>

}