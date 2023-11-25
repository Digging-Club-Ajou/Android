package com.ajou.diggingclub.ground.models

import com.google.gson.annotations.SerializedName

data class FavoritesModel(
    @SerializedName("melodyCardId") val melodyCardId : Int,
    @SerializedName("songTitle") val songTitle : String,
    @SerializedName("artistName") val artistName : String,
    @SerializedName("albumCoverImageUrl") val albumCoverImageUrl : String
)
