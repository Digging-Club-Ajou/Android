package com.ajou.diggingclub.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MemberApi {
    @GET("member/search")
    fun searchMember(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Query("keyword") keyword : String
    ):Call<ResponseBody>
}