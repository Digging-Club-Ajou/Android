package com.ajou.diggingclub.intro

data class IntroSelectModel(
    val text : String,
    val image : Int?,
    val imageUrl : String?,
    var selected : Boolean = false,
    val genre : String?
)
