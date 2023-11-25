package com.ajou.diggingclub.network.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ArtistService {
    @POST("artists")
    fun postArtists(
        @Header("AccessToken") token : String,
        @Header("RefreshToken") refreshToken : String,
        @Body artists : RequestBody
    ) : Call<ResponseBody>
}