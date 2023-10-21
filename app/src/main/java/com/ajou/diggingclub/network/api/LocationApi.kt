package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.LocationResponse
import com.ajou.diggingclub.network.models.SpotifyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LocationApi {
    @GET("location")
    fun searchLocation(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Query("query") query : String,
        @Query("x") x : String,
        @Query("y") y : String
    ) : Call<LocationResponse> // 차후에 responseBody 대신 적절한 model 생성
}