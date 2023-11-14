package com.ajou.diggingclub.ground.models

import com.google.gson.annotations.SerializedName

data class FollowingModel(
    @SerializedName("memberId") val memberId : String,
    @SerializedName("albumId") val albumId : String,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("isFollowForFollow") val isFollowForFollow : String
)
