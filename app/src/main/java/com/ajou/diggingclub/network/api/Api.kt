package com.ajou.diggingclub.network.api

import com.ajou.diggingclub.network.models.SignUpRequestBody
import com.ajou.diggingclub.network.models.SignUpResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {
    @GET("test/data")
    fun testCommunication() : String

    @Headers("Content-Type:application/json")
    @POST("api/signup")
    fun signUpUser(@Body userInfo : SignUpRequestBody) : Call<SignUpResponseBody>

}