package com.ajou.diggingclub.melody.models

import com.google.gson.annotations.SerializedName

data class MelodyCardModel(
    @SerializedName("artistName") val artistName: String,
    @SerializedName("songTitle") val songTitle: String,
    @SerializedName("genre") val genre: String?,
    @SerializedName("previewUrl") val previewUrl: String,
    @SerializedName("address") val address: String?,
    @SerializedName("cardDescription") val cardDescription: String?,
    @SerializedName("color") val color: String
)
