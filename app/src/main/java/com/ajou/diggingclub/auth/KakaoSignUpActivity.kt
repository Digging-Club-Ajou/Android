package com.ajou.diggingclub.auth

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ajou.diggingclub.BuildConfig
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.intro.IntroActivity
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumApi
import com.ajou.diggingclub.network.api.UserApi
import com.ajou.diggingclub.network.models.TokenResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class KakaoSignUpActivity : AppCompatActivity() {
    val clientId = BuildConfig.CLIENT_ID // apikey.properties에 있음
    val redirectUri = BuildConfig.REDIRECT_URI
    val responseType = "code"

    private lateinit var viewModel : KakaoSignUpViewModel

    private val client = RetrofitInstance.getInstance().create(UserApi::class.java)
    private val albumClient = RetrofitInstance.getInstance().create(AlbumApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_sign_up)
        val dataStore = UserDataStore()

        viewModel = ViewModelProvider(this)[KakaoSignUpViewModel::class.java]

        val webview : WebView = findViewById(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.setSupportMultipleWindows(true)
        webview.webViewClient = object : WebViewClient(){
            val target = redirectUri+"?code=" // code 앞에 들어갈 것은 redirectURI
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url!!.substring(target.indices) == target) {
                    val code = url!!.substring(target.length)
                    viewModel.setAuthCode(code)
                }
            }
        }

        webview.loadUrl("https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}")

        viewModel.authCode.observe(this, Observer{
            if(viewModel.authCode.value!!.isNotEmpty()){
                val jsonObject = JsonObject().apply {
                    addProperty("authCode", viewModel.authCode.value.toString())
                }
                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
                var tokens : TokenResponse? = null
                client.login(requestBody).enqueue(object : Callback<TokenResponse>{
                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        if(response.isSuccessful){
                            tokens = response.body()
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(tokens!!.accessToken)
                                dataStore.saveRefreshToken(tokens!!.refreshToken)
                            }
                            if(tokens!!.isNew){
                            val intent = Intent(this@KakaoSignUpActivity, IntroActivity::class.java)
                            startActivity(intent)
                            }else{
                                CoroutineScope(Dispatchers.IO).launch {
                                    val nicknameDeferred = async { client.getNickname(tokens!!.accessToken, tokens!!.refreshToken) }
                                    val albumExistDeferred = async { albumClient.checkAlbumsExist(tokens!!.accessToken, tokens!!.refreshToken) }
                                    val userInfoDeferred = async { client.getUserInfo(tokens!!.accessToken, tokens!!.refreshToken) }

                                    val nicknameResponse = nicknameDeferred.await()
                                    val albumExistResponse = albumExistDeferred.await()
                                    val userInfoResponse = userInfoDeferred.await()

                                    dataStore.saveFirstFlag(true)

                                    if (nicknameResponse.isSuccessful && albumExistResponse.isSuccessful) {
                                        val nicknameBody = JSONObject(nicknameResponse.body()?.string())
                                        dataStore.saveNickname(nicknameBody.get("nickname").toString())
                                        val albumExistBody = JSONObject(albumExistResponse.body()?.string())
                                        if (albumExistBody.get("alreadyExist") == false) dataStore.saveAlbumExistFlag(false)
                                        else dataStore.saveAlbumExistFlag(true)
                                        val userInfoBody = JSONObject(userInfoResponse.body()?.string())
                                        dataStore.saveMemberId(userInfoBody.get("memberId").toString().toInt())
                                        dataStore.saveAlbumId(userInfoBody.get("albumId").toString().toInt())
                                    }
                                }
                                val intent = Intent(this@KakaoSignUpActivity, GroundActivity::class.java)
                                startActivity(intent)
                            }
//                           webview.removeAllViews() // TODO 흰 화면 보일 때 처리 어떻게 할 것인지 + redirectURI로 이동했을 때 뜨는 에러페이지 보일 때 어떻게 할 것인지
//                           webview.destroy()
                        }else {
                            Log.d("response not successful",response.errorBody()?.string().toString())
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Log.d("fail",t.message.toString())
                    }
                })
            }
        })
    }

}