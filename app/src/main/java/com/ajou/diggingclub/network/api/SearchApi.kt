package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.MemberSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApi {
    @GET("member/search")
    fun searchMember(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Query("keyword") keyword : String
    ) : Call<MemberSearchResponse>
}