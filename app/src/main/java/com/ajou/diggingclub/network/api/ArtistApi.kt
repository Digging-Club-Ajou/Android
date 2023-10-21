package com.ajou.diggingclub.network.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ArtistApi {
    @POST("artists")
    fun postArtists(
        @Header("AccessToken") token : String,
        @Header("RefreshToken") refreshToken : String,
        @Body artists : RequestBody
    ) : Call<ResponseBody>
}