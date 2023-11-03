package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.ground.models.NotificationsModel
import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("notificationResponses")
    val notificationsList : List<NotificationsModel> = arrayListOf()
)
