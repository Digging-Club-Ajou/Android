package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.ground.models.MemberSearchModel
import com.google.gson.annotations.SerializedName

data class MemberSearchResponse(
    @SerializedName("memberSearchResponses") val memberSearchListResult : List<MemberSearchModel>
)
