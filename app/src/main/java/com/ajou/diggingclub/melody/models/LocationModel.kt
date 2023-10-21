package com.ajou.diggingclub.melody.models

import com.google.gson.annotations.SerializedName

data class LocationModel(
    @SerializedName("placeName") val name : String,
    @SerializedName("distance") val distance : String,
)
