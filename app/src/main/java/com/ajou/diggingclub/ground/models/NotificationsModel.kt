package com.ajou.diggingclub.ground.models

import com.google.gson.annotations.SerializedName

data class NotificationsModel(
    @SerializedName("notificationId") val notificationId : Int,
    @SerializedName("message") val message : String,
    @SerializedName("minutes") val minutes : String
)
