package com.ajou.diggingclub.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://3.34.171.76:8080/" // const val은 컴파일 시간 동안 할당되어야 함, 함수 같은 거 할당 x

    private val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance() : Retrofit {
        return client
    }



}