package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.SpotifyResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicService {
    @GET("musics")
    fun searchMusic(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Query("search") search : String,
        @Query("page") page : String
    ) : Call<SpotifyResponse>

    @GET("musics/play-record")
    suspend fun getMyRecord(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Response<ResponseBody>

    @POST("musics/play-record")
    fun addCount(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Body musicInfo : RequestBody
    ) : Call<ResponseBody>

    @GET("musics/play-record/{memberId}")
    suspend fun getUserRecord(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path("memberId") memberId : String
    ) : Response<ResponseBody>
}