package com.ajou.diggingclub.network.models

data class TokenResponse(
    val accessToken : String,
    val refreshToken : String,
    val isNew : Boolean
)
