package com.ajou.diggingclub.network.models

import com.google.gson.annotations.SerializedName

data class EditAlbum(
    @SerializedName("albumName") val albumName: String?
)
