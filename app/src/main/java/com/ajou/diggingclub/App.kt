package com.ajou.diggingclub

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk

class App : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance : App? = null

        fun context() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.NATIVE_API_KEY)
//        Log.d("hash",Utility.getKeyHash(this))
    }
}