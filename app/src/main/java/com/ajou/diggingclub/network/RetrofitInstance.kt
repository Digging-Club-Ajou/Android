package com.ajou.diggingclub.network

import com.ajou.diggingclub.network.models.NicknameErrorBody
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody
import okhttp3.internal.authenticator.JavaNetAuthenticator
import retrofit2.Converter
import java.lang.reflect.Type
import java.net.CookieManager

object RetrofitInstance {

    private const val BASE_URL =

    var builder = OkHttpClient().newBuilder()
    var okHttpClient = builder
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .build()

    val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(NullOnEmptyConverterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance() : Retrofit {
        return client
    }

    class NullOnEmptyConverterFactory: Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit)
        = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

            override fun convert(value: ResponseBody) : Any? =
                if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }

    }

}

