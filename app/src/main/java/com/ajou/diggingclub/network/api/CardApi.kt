package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.ajou.diggingclub.melody.models.SendingMelodyCardModel
import com.ajou.diggingclub.network.models.MelodyCardResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CardApi {
    @Multipart
    @POST("melody-cards")
    fun createCard(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Part("melodyCardRequest") melodyCardRequest : SendingMelodyCardModel,
        @Part file : MultipartBody.Part?
    ) : Call<ResponseBody>

    @GET("melody-cards")
    fun getAllCards(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
    ) : Call<MelodyCardResponse>

    @GET("melody-cards/{melodyCardId}")
    fun getCard(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path(value = "melodyCardId") melodyCardId : String
    ) : Call<ReceivedMelodyCardModel>

    @GET("melody-cards/album/{albumId}")
    fun getAlbumCards(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path(value = "albumId") albumId : String
    ) : Call<MelodyCardResponse>
}