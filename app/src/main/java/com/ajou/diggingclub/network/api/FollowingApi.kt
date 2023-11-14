package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.FollowingResponseBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowingApi {
    @POST("following")
    fun postFollowing(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Body following : RequestBody
    ) : Call<ResponseBody>

    @DELETE("following")
    fun deleteFollowing(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Call<ResponseBody>

    @GET("followings")
    fun getFollowings(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path("memberId") memberId : Int
    ) : Call<FollowingResponseBody>
}