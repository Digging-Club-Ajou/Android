package com.ajou.diggingclub.start

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.viewModels
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.auth.KakaoSignUpActivity
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.intro.IntroActivity
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.api.UserService
import com.ajou.diggingclub.profile.MyAlbumViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel : MyAlbumViewModel by viewModels()
    private val client = RetrofitInstance.getInstance().create(AlbumService::class.java)
    private val userClient = RetrofitInstance.getInstance().create(UserService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timber.plant(Timber.DebugTree())

        val imageView : ImageView = findViewById(R.id.imageView2)
        val video : VideoView = findViewById(R.id.video)
        val loadingText = findViewById<TextView>(R.id.loadingText)
        val diggleText = findViewById<TextView>(R.id.diggle)
        val description = findViewById<TextView>(R.id.text)
        var accessToken : String? = null
        var refreshToken : String? = null

        val videoPath = "android.resource://" + packageName + "/" + R.raw.splashloading
        val uri = Uri.parse(videoPath)

        video.setOnPreparedListener { mediaPlayer ->
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = video.width / video.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                video.scaleX = scaleX
            } else {
                video.scaleY = 1f / scaleX
            }
        }

        video.setVideoURI(uri)

        video.setOnCompletionListener { mediaPlayer ->
            video.stopPlayback()
        }

        video.start()  // video 관련 코드

        val dataStore = UserDataStore()

        // 일정 시간 후에 두 번째 GIF 이미지로 변경
        Handler().postDelayed({
            if(this.isDestroyed) throw Exception("액티비티가 종료되었습니다.") // TODO 추후 처리사항 좀 더 고민해보기 activity destroyed
            description.visibility = View.GONE
            Glide.with(this)
                .asGif()
                .load(R.drawable.loadinganimation)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView)
            val gifDrawable2 = pl.droidsonroids.gif.GifDrawable(resources, R.drawable.loadinganimation)
            imageView.setImageDrawable(gifDrawable2)
            gifDrawable2.setSpeed(0.8f)

            imageView.visibility = View.VISIBLE
            loadingText.visibility = View.VISIBLE
            diggleText.visibility = View.VISIBLE

            if(viewModel.first.value == false){
                val intent = Intent(this@SplashActivity, LandingActivity::class.java)
                startActivity(intent)
//                finish()
            }else{
                CoroutineScope(Dispatchers.IO).launch {
                    accessToken = dataStore.getAccessToken().toString()
                    refreshToken = dataStore.getRefreshToken().toString()
//                    Log.d("accessToken",accessToken.toString())

                    if(accessToken == null || refreshToken == null){
                        val intent = Intent(this@SplashActivity, LandingActivity::class.java)
                        startActivity(intent)
//                    finish()
                    }
                    try {
                        val nicknameDeferred = async { userClient.getNickname(accessToken, refreshToken!!) }
                        val albumExistDeferred = async { client.checkAlbumExist(accessToken!!, refreshToken!!) }
                        val userInfoDeferred = async { userClient.getUserInfo(accessToken!!, refreshToken!!) }

                        val nicknameResponse = nicknameDeferred.await()
                        val albumExistResponse = albumExistDeferred.await()
                        val userInfoResponse = userInfoDeferred.await()

                        if(!nicknameResponse.isSuccessful&&nicknameResponse.code() == 404){
                            Log.d("nickname",nicknameResponse.errorBody()?.string().toString())
                            val intent = Intent(this@SplashActivity, IntroActivity::class.java)
                            startActivity(intent)
                        } else if(!nicknameResponse.isSuccessful&&nicknameResponse.code() == 401){
                            Log.d("nickname",nicknameResponse.errorBody()?.string().toString())
                            val intent = Intent(this@SplashActivity, LandingActivity::class.java)
                            startActivity(intent)
                        }
                        else if (nicknameResponse.isSuccessful && albumExistResponse.isSuccessful && userInfoResponse.isSuccessful) {
                            if(nicknameResponse.headers()["AccessToken"] != null) dataStore.saveAccessToken(nicknameResponse.headers()["AccessToken"].toString())
//                            Log.d("accessToken",accessToken.toString())
                            val nicknameBody = JSONObject(nicknameResponse.body()?.string())
                            dataStore.saveNickname(nicknameBody.get("nickname").toString())
                            viewModel.setNickname(nicknameBody.get("nickname").toString())
//                            Log.d("nickname",nicknameBody.get("nickname").toString())
                            val albumExistBody = JSONObject(albumExistResponse.body()?.string())
                            if (albumExistBody.get("alreadyExist") == false) dataStore.saveAlbumExistFlag(false)
                            else dataStore.saveAlbumExistFlag(true)
                            val userInfoBody = JSONObject(userInfoResponse.body()?.string())
                            dataStore.saveMemberId(userInfoBody.get("memberId").toString())
                            viewModel.setUserId(userInfoBody.get("memberId").toString())
                            dataStore.saveAlbumId(userInfoBody.get("albumId").toString())
                            viewModel.setAlbumId(userInfoBody.get("albumId").toString())
                            val intent = Intent(this@SplashActivity, GroundActivity::class.java)
                            startActivity(intent)
//                        finish()
                        } else {
                            // 처리 실패 시의 로직
//                            Log.d("tokens",refreshToken.toString())
//                            Log.d("tokenss",accessToken.toString())
//                            Log.d("API call failed", "Nickname: ${nicknameResponse.errorBody()?.string()}, Album Exist: ${albumExistResponse.errorBody()?.string()}")
                            val intent = Intent(this@SplashActivity, LandingActivity::class.java)
                            startActivity(intent)
//                        finish()
                        }
                    }catch (e:Exception){
                        Log.d("fail in catch",e.message.toString())
                        Firebase.crashlytics.recordException(e)
                    }

                }
            }
        }, 2000)
        viewModel.getFirstFlag()
        viewModel.getExistFlag()
    }

}