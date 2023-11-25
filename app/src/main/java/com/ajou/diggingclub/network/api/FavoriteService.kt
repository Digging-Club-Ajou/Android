package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.FavoritesResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FavoriteService {
    @GET("card-favorites")
    suspend fun getMyFavoriteList(
        @Header("AccessToken") accessToken: String,
        @Header("RefreshToken") refreshToken: String
    ): Response<FavoritesResponse>

    @GET("card-favorites/likes/{melodyCardId}")
    suspend fun getFavoriteStatus(
        @Header("AccessToken") accessToken: String,
        @Header("RefreshToken") refreshToken: String,
        @Path("melodyCardId") melodyCardId: String
    ) : Response<ResponseBody>

    @POST("card-favorites/likes/{melodyCardId}")
    suspend fun postFavorite(
        @Header("AccessToken") accessToken: String,
        @Header("RefreshToken") refreshToken: String,
        @Path("melodyCardId") melodyCardId: String
    ) : Response<ResponseBody>

    @DELETE("card-favorites/likes/{melodyCardId}")
    suspend fun deleteFavorite(
        @Header("AccessToken") accessToken: String,
        @Header("RefreshToken") refreshToken: String,
        @Path("melodyCardId") melodyCardId: String
    ) : Response<ResponseBody>

    @GET("card-favorites/members/{memberId}")
    suspend fun getFavoriteList(
        @Header("AccessToken") accessToken: String,
        @Header("RefreshToken") refreshToken: String,
        @Path("memberId") memberId: String
    ) : Response<FavoritesResponse>
}