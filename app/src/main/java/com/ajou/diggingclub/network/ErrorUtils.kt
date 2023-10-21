package com.ajou.diggingclub.network

import com.ajou.diggingclub.network.models.NicknameErrorBody
import okhttp3.ResponseBody

class ErrorUtils {
    object ErrorUtils {
        fun getErrorResponse(errorBody: ResponseBody): NicknameErrorBody? {
            return RetrofitInstance.client.responseBodyConverter<NicknameErrorBody>(
                NicknameErrorBody::class.java,
                NicknameErrorBody::class.java.annotations
            ).convert(errorBody)
        }
    }
}