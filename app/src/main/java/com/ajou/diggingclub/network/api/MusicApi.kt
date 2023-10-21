package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.SpotifyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MusicApi {
    @GET("musics")
    fun searchMusic(
        @Header("AccessToken") accessToken : String,
        @Header("refreshToken") refreshToken : String,
        @Query("search") search : String,
    ) : Call<SpotifyResponse> // 차후에 responseBody 대신 적절한 model 생성
}