package com.ajou.diggingclub.network.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GenreService {
    @POST("genres")
    fun postGenre(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Body genre : RequestBody
    ) : Call<ResponseBody>
}