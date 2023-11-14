package com.ajou.diggingclub.ground.models

import com.google.gson.annotations.SerializedName

data class ReceivedMelodyCardModel(
    @SerializedName("melodyCardId") val melodyCardId: Int,
    @SerializedName("albumId") val albumId: Int,
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("songTitle") val songTitle: String,
    @SerializedName("previewUrl") val previewUrl: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("address") val address: String?,
    @SerializedName("cardDescription") val cardDescription: String?,
    @SerializedName("color") val color: String,
    var isPlaying : Boolean = false
)
