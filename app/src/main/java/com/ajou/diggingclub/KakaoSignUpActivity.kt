package com.ajou.diggingclub

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ajou.diggingclub.network.KakaoRetrofitInstance
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.Api
import com.ajou.diggingclub.network.api.KakaoAuthApi
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class KakaoSignUpActivity : AppCompatActivity() {
    val clientId = BuildConfig.CLIENT_ID // apikey.properties에 있음
    val redirectUri = BuildConfig.REDIRECT_URI
    val responseType = "code"

    private lateinit var viewModel : KakaoSignUpViewModel

    private val client = RetrofitInstance.getInstance().create(Api::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_sign_up)

        viewModel = ViewModelProvider(this)[KakaoSignUpViewModel::class.java]

        val webview : WebView = findViewById(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.setSupportMultipleWindows(true)
        webview.webViewClient = object : WebViewClient(){
            val target = redirectUri+"?code=" // code 앞에 들어갈 것은 redirectURI
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("debug",url!!)
                if (url!!.substring(target.indices) == target) {
                    val code = url!!.substring(target.length)
                    Log.d("debug",code)
                    viewModel.setAuthCode(code)
                }
            }
        }

        webview.loadUrl("https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}")

        viewModel.authCode.observe(this, Observer{
            Log.d("viewModel.authcode",viewModel.authCode.value.toString())
            if(viewModel.authCode.value!!.isNotEmpty()){
                client.login(viewModel.authCode.value.toString()).enqueue(object : Callback<String>{
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                       if(response.isSuccessful){
                           Log.d("success",response.body().toString())
                           val intent = Intent(this@KakaoSignUpActivity,IntroActivity::class.java)
                           intent.putExtra("authCode",viewModel.authCode.value)
                           startActivity(intent)
//                           webview.removeAllViews() // 흰 화면 보일 때 처리 어떻게 할 것인지 + redirectURI로 이동했을 때 뜨는 에러페이지 보일 때 어떻게 할 것인지
//                           webview.destroy()
                       }else {
                           Log.d("response not successful",response.errorBody()?.string().toString())
                       }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("fail",t.message.toString())
                    }

                })

            }
        })

//        val btn : Button = findViewById(R.id.kakaologin)

//        btn.setOnClickListener {
//            client.kakaoAuthorize(clientId,redirectUri,responseType).enqueue(object : Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    if(response.isSuccessful){
//                        Log.d("success",response.body().toString())
//                        Log.d("success",response.message().toString())
//                        Log.d("success",response.raw().toString())
//                    }else {
//                        Log.d("response not successful",response.errorBody()?.string().toString())
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d("fail",t.message.toString())
//                }
//
//            })
//            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@KakaoSignUpActivity)) {
//                UserApiClient.instance.loginWithKakaoTalk(this@KakaoSignUpActivity, callback = callback)
//            } else {
//                UserApiClient.instance.loginWithKakaoAccount(this@KakaoSignUpActivity, callback = callback)
//            }
//        }

    }

}