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

interface AlbumApi {
    @GET("albums-image")
    fun getAlbumsImage(
        @Header("AccessToken") accessToken : String,
//        @Header("RefreshToken") refreshToken : String
    ) : Call<ResponseBody>

    @POST("albums-validation")
    fun checkAlbumsExist(
        @Header("AccessToken") token : String,
//        @Header("RefreshToken") refreshToken : String
    ) : Call<ResponseBody>

    @POST("albums/name-validation")
    fun checkAlbumsName(
        @Header("AccessToken") token : String,
        @Header("RefreshToken") refreshToken : String,
        @Body albumName : RequestBody
    ) : Call<ResponseBody>

    @Multipart
    @POST("albums")
    fun createAlbum(
        @Header("AccessToken") token : String,
        @Header("RefreshToken") refreshToken : String,
        @Part("albumNameRequest") albumNameRequest : RequestBody,
        @Part file : MultipartBody.Part
    ) : Call<ResponseBody>
}