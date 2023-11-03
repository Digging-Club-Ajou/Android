package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.google.gson.annotations.SerializedName

data class MelodyCardResponse(
    @SerializedName("melodyCardResponses")
    val melodyCardListResult : List<ReceivedMelodyCardModel> = arrayListOf()
)
