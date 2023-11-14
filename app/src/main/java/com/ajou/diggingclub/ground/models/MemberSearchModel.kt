package com.ajou.diggingclub.ground.models

import com.google.gson.annotations.SerializedName

data class MemberSearchModel(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("albumId") val albumId: String
)
