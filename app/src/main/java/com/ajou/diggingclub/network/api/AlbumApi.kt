package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.network.models.FollwingAlbumResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface AlbumApi {
    @GET("albums-image")
    fun getAlbumsImage(
        @Header("AccessToken") accessToken : String,
//        @Header("RefreshToken") refreshToken : String
    ) : Call<ResponseBody>

    @POST("albums-validation")
    fun checkAlbumsExist(
        @Header("AccessToken") accessToken : String,
//        @Header("RefreshToken") refreshToken : String
    ) : Call<ResponseBody>

    @POST("albums/name-validation")
    fun checkAlbumsName(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Body albumName : RequestBody
    ) : Call<ResponseBody>

    @Multipart
    @POST("albums")
    fun createAlbum(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Part("albumNameRequest") albumNameRequest : RequestBody,
        @Part file : MultipartBody.Part
    ) : Call<ResponseBody>

    @GET("albums/following")
    fun getFollowingAlbums(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Call<FollwingAlbumResponse>

    @GET("albums/{albumId}")
    fun getAlbum(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path(value = "albumId") albumId : String
    ) : Call<ReceivedAlbumModel>
}