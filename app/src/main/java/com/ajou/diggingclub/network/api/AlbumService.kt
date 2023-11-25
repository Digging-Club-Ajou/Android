package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.network.models.AlbumResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AlbumService {
    @GET("albums")
    fun getMyAlbum(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Call<ReceivedAlbumModel>

    @POST("albums-validation")
    suspend fun checkAlbumExist(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Response<ResponseBody>

    @GET("albums/following")
    fun getFollowingAlbums(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Call<AlbumResponse>

    @POST("albums/name-validation")
    fun checkAlbumsName(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Body albumName : RequestBody
    ) : Call<ResponseBody>

    @GET("albums/recommendation-ai")
    fun getAIAlbums(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Call<AlbumResponse>

    @GET("albums/recommendation-genres")
    fun getGenreAlbums(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Call<AlbumResponse>

    @GET("albums/{albumId}")
    fun getAlbum(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path(value = "albumId") albumId : String
    ) : Call<ReceivedAlbumModel>

    @Multipart
    @POST("albums")
    fun createAlbum(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Part("albumNameRequest") albumNameRequest : RequestBody,
        @Part file : MultipartBody.Part
    ) : Call<ResponseBody>

    @Multipart
    @POST("albums/update")
    fun updateAlbum(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Part("albumNameRequest") albumNameRequest : RequestBody,
        @Part file : MultipartBody.Part?
    )
}