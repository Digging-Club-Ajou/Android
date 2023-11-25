package com.ajou.diggingclub.network.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAuthService {
    @GET("oauth/authorize")
    fun kakaoAuthorize(
        @Query("client_id") clientId : String,
        @Query("redirect_uri") redirectUri : String,
        @Query("response_type") responseType : String
    ) : Call<String>
}