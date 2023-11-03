package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.google.gson.annotations.SerializedName

data class FollwingAlbumResponse(
    @SerializedName("albumResponses")
    val albumListResult : List<ReceivedAlbumModel> = arrayListOf()
)
