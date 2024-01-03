package com.ajou.diggingclub.auth

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ajou.diggingclub.BuildConfig
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.intro.IntroActivity
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.api.UserService
import com.ajou.diggingclub.network.models.TokenResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class KakaoSignUpActivity : AppCompatActivity() {
    val clientId = BuildConfig.CLIENT_ID // apikey.properties에 있음
    val redirectUri = BuildConfig.REDIRECT_URI
    val responseType = "code"

    private lateinit var viewModel : KakaoSignUpViewModel

    private val userService = RetrofitInstance.getInstance().create(UserService::class.java)
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_sign_up)
        val dataStore = UserDataStore()
        viewModel = ViewModelProvider(this)[KakaoSignUpViewModel::class.java]

        val webview : WebView = findViewById(R.id.webview)
        webview.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        webview.clearCache(true)
        webview.clearHistory() // webview 캐시 지워줘야 카카오 로그인 시 자동로그인 막음
        webview.clearFormData()
        webview.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                val target = redirectUri.plus("?code=")// code 앞에 들어갈 것은 redirectURI
                Timber.d("code url $url")
                if (url!!.substring(target.indices) == target) {
                    val code = url!!.substring(target.length)
                    Timber.d("code $code")
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
                Timber.d("jsonObject $jsonObject")
                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
                var tokens : TokenResponse? = null
                userService.login(requestBody).enqueue(object : Callback<TokenResponse>{
                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        if(response.isSuccessful){
                            tokens = response.body()
                            Timber.d("response ${response.body()}")
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(tokens!!.accessToken)
                                dataStore.saveRefreshToken(tokens!!.refreshToken)
                            }
                            if(tokens!!.isNew){
                            val intent = Intent(this@KakaoSignUpActivity, IntroActivity::class.java)
                            startActivity(intent)
                            }else{
                                CoroutineScope(Dispatchers.IO).launch {
                                    val nicknameDeferred = async { userService.getNickname(tokens!!.accessToken, tokens!!.refreshToken) }
                                    val albumExistDeferred = async { albumService.checkAlbumExist(tokens!!.accessToken, tokens!!.refreshToken) }
                                    val userInfoDeferred = async { userService.getUserInfo(tokens!!.accessToken, tokens!!.refreshToken) }

                                    val nicknameResponse = nicknameDeferred.await()
                                    val albumExistResponse = albumExistDeferred.await()
                                    val userInfoResponse = userInfoDeferred.await()

                                    dataStore.saveFirstFlag(true)
//                                    Log.d("saved",tokens!!.refreshToken)
//                                    Log.d("saved!!",tokens!!.accessToken)

                                    if (nicknameResponse.isSuccessful && albumExistResponse.isSuccessful && userInfoResponse.isSuccessful) {
                                        val nicknameBody = JSONObject(nicknameResponse.body()?.string())
                                        dataStore.saveNickname(nicknameBody.get("nickname").toString())
                                        val albumExistObject = JSONObject(albumExistResponse.body()?.string())
                                        if (albumExistObject.get("alreadyExist") == false) dataStore.saveAlbumExistFlag(false)
                                        else dataStore.saveAlbumExistFlag(true)
                                        val userInfoBody = JSONObject(userInfoResponse.body()?.string())
                                        dataStore.saveMemberId(userInfoBody.get("memberId").toString())
                                        dataStore.saveAlbumId(userInfoBody.get("albumId").toString())
                                        val intent = Intent(this@KakaoSignUpActivity, GroundActivity::class.java)
                                        startActivity(intent)
                                    }else{
//                                        Log.d("nicknameerror response",nicknameResponse.errorBody()?.string().toString())
//                                        Log.d("albumexisterror response",albumExistResponse.errorBody()?.string().toString())
//                                        Log.d("userinfoerror response",userInfoResponse.errorBody()?.string().toString())
                                        val intent = Intent(this@KakaoSignUpActivity, IntroActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }
//                           webview.removeAllViews() // TODO 흰 화면 보일 때 처리 어떻게 할 것인지 + redirectURI로 이동했을 때 뜨는 에러페이지 보일 때 어떻게 할 것인지
//                           webview.destroy()
                        }else {
                            Timber.d("response error ${response.errorBody()?.string().toString()}")
//                            Log.d("response not successful",response.errorBody()?.string().toString())
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
//                        Log.d("fail",t.message.toString())
                    }
                })
            }
        })
    }
    private val tabConnection = object : CustomTabsServiceConnection() {

        override fun onCustomTabsServiceConnected(componentName: ComponentName, customTabsClient: CustomTabsClient) {
            val session = customTabsClient.newSession(object : CustomTabsCallback() {
                override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                    super.onNavigationEvent(navigationEvent, extras)
                    when (navigationEvent) {
                        CustomTabsCallback.NAVIGATION_STARTED ->
                            Log.e("psbs", "NAVIGATION_STARTED")
                        CustomTabsCallback.NAVIGATION_FINISHED ->
                            Log.e("psbs", "NAVIGATION_FINISHED")
                        CustomTabsCallback.NAVIGATION_FAILED ->
                            Log.e("psbs", "NAVIGATION_FAILED")
                        CustomTabsCallback.NAVIGATION_ABORTED ->
                            Log.e("psbs", "NAVIGATION_ABORTED")
                    }
                }
            })

            val customTabsIntentBuilder = CustomTabsIntent.Builder(session)


            val url = "https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}"
            val customTabsIntent = customTabsIntentBuilder
                .build()
            customTabsIntent.intent.`package` = "com.android.chrome"
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            customTabsIntent.launchUrl(this@KakaoSignUpActivity, Uri.parse(url))
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // Handle service disconnection
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.toString() ?: return
        Log.d("intent",intent.data.toString())
        viewModel.setAuthCode(intent.data.toString().substringAfter("code="))
    }
    override fun onBackPressed() {

    }
}