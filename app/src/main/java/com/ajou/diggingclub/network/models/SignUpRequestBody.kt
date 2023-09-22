package com.ajou.diggingclub.network.models

data class SignUpRequestBody(
    val username : String,
    val loginId : String,
    val password : String,
    val passwordCheck : String,
    val phoneNumber : String,
    val email : String,
    val gender : String
)
