package com.ajou.diggingclub.network.models

import com.ajou.diggingclub.ground.models.FavoritesModel
import com.google.gson.annotations.SerializedName

data class FavoritesResponse(
    @SerializedName("cardFavoriteResponses") val cardFavoriteResponses: List<FavoritesModel>
)
