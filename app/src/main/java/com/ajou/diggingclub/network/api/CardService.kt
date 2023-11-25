package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.ajou.diggingclub.melody.models.SendingMelodyCardModel
import com.ajou.diggingclub.network.models.MelodyCardResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface CardService {
    @GET("melody-cards/{melodyCardId}")
    fun getCard(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path(value = "melodyCardId") melodyCardId : String
    ) : Call<ReceivedMelodyCardModel>

    @GET("melody-cards/albums/{albumId}")
    suspend fun getAlbumCards(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path(value = "albumId") albumId : String
    ) : Response<MelodyCardResponse>

    @DELETE("melody-cards/{melodyCardId}")
    fun deleteCard(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path (value = "melodyCardId") melodyCardId : String
    ) : Call<ResponseBody>

    @Multipart
    @POST("melody-cards")
    fun createCard(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Part("melodyCardRequest") melodyCardRequest : SendingMelodyCardModel,
        @Part file : MultipartBody.Part?
    ) : Call<ResponseBody>

//    @Multipart
//    @POST("melody-cards/update")
//    fun updateCard(
//        @Header("AccessToken") accessToken : String,
//        @Header("RefreshToken") refreshToken : String,
//        @Part("melodyCardRequest") melodyCardRequest : SendingMelodyCardModel,
//        @Part file : MultipartBody.Part?
//    )
}