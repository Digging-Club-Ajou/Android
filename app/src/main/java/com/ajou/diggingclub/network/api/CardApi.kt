package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.melody.models.MelodyCardModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CardApi {

    @Multipart
    @POST("melody-cards")
    fun createCard(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Part("melodyCardRequest") melodyCardRequest : MelodyCardModel,
        @Part file : MultipartBody.Part?
    ) : Call<ResponseBody>
}