package com.ajou.diggingclub.ground.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceivedAlbumModel(
    @SerializedName("memberId") val memberId : Int,
    @SerializedName("albumId") val albumId : Int,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("albumName") val albumName : String,
    @SerializedName("imageUrl") val imageUrl : String,
    @SerializedName("artistNames") val artistNames : ArrayList<String>
): Parcelable
