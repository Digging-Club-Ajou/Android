package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.melody.models.LocationModel
import com.google.gson.annotations.SerializedName

data class LocationResponse (
    @SerializedName("locationResponses")
    val locationListResult : List<LocationModel> = arrayListOf()
        )