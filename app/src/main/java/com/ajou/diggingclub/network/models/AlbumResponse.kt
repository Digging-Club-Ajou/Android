package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.google.gson.annotations.SerializedName

data class AlbumResponse(
    @SerializedName("albumResponses")
    val albumListResult : List<ReceivedAlbumModel> = arrayListOf()
)
