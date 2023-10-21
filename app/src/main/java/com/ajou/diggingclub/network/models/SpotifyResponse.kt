package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.google.gson.annotations.SerializedName

data class SpotifyResponse (
    @SerializedName("spotifySearchDtos")
    val spotifyListResult : List<MusicSpotifyModel> = arrayListOf()
        )