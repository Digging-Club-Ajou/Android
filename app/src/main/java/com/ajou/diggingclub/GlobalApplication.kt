package com.ajou.diggingclub

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.NATIVE_API_KEY)

        Log.d("hash",Utility.getKeyHash(this))
    }
}