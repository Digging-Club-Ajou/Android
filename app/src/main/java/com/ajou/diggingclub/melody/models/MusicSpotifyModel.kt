package com.ajou.diggingclub.melody.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicSpotifyModel(
    @SerializedName("artistName") val artist : String,
    @SerializedName("songTitle") val title : String,
    @SerializedName("imageUrl") val imageUrl : String,
    @SerializedName("previewUrl") val previewUrl : String
) : Parcelable
