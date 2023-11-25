package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.NotificationsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface NotificationService {
    @GET("notifications")
    fun getNotification(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String
    ) : Call<NotificationsResponse>

    @DELETE("notifications/{notificationId}")
    fun deleteNotification(
        @Header("AccessToken") accessToken : String,
        @Header("RefreshToken") refreshToken : String,
        @Path("notificationId") notificationId : String
    ) : Call<ResponseBody>
}