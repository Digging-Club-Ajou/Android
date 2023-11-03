package com.ajou.diggingclub.ground.models

import com.google.gson.annotations.SerializedName

data class MemberSearchModel(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("nickname") val nickname: String
)
