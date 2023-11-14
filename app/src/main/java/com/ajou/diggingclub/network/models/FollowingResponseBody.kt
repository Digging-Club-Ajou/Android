package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.ground.models.FollowingModel
import com.google.gson.annotations.SerializedName

data class FollowingResponseBody(
    @SerializedName("followings") val followings: List<FollowingModel>,
    @SerializedName("followers") val followers: List<FollowingModel>
)
