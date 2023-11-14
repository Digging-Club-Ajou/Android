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
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.melody.MelodyActivity
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumApi
import com.ajou.diggingclub.network.api.UserApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private val viewModel : StartViewModel by viewModels()
    private val client = RetrofitInstance.getInstance().create(AlbumApi::class.java)
    private val userClient = RetrofitInstance.getInstance().create(UserApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView : ImageView = findViewById(R.id.imageView2)
        val video : VideoView = findViewById(R.id.video)
        val loadingText = findViewById<TextView>(R.id.loadingText)
        val diggleText = findViewById<TextView>(R.id.diggle)
        val description = findViewById<TextView>(R.id.text)
        var accessToken : String? = null
        var refreshToken : String? = null

        val videoPath = "android.resource://" + packageName + "/" + R.raw.splashloading
        val uri = Uri.parse(videoPath)
        video.setVideoURI(uri)

        video.setOnCompletionListener { mediaPlayer ->
            video.stopPlayback()
        }

        video.start()  // video 관련 코드

        val dataStore = UserDataStore()

        // 일정 시간 후에 두 번째 GIF 이미지로 변경
        Handler().postDelayed({
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
                Log.d("here1", "here1")
                val intent = Intent(this@SplashActivity, LandingActivity::class.java)
                startActivity(intent)
                finish()
            }
            Log.d("here2", "here2")
            CoroutineScope(Dispatchers.IO).launch {
                accessToken = dataStore.getAccessToken().toString()
                refreshToken = dataStore.getRefreshToken().toString()

                if(accessToken == null || refreshToken == null){
                    val intent = Intent(this@SplashActivity, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                Log.d("here6", accessToken.toString())
                val nicknameDeferred = async { userClient.getNickname(accessToken, refreshToken!!) }
                val albumExistDeferred = async { client.checkAlbumsExist(accessToken!!, refreshToken!!) }
                val userInfoDeferred = async { userClient.getUserInfo(accessToken!!, refreshToken!!) }

                val nicknameResponse = nicknameDeferred.await()
                val albumExistResponse = albumExistDeferred.await()
                val userInfoResponse = userInfoDeferred.await()

                if (nicknameResponse.isSuccessful && albumExistResponse.isSuccessful && userInfoResponse.isSuccessful) {
                    val nicknameBody = JSONObject(nicknameResponse.body()?.string())
                    dataStore.saveNickname(nicknameBody.get("nickname").toString())
                    val albumExistBody = JSONObject(albumExistResponse.body()?.string())
                    if (albumExistBody.get("alreadyExist") == false) dataStore.saveAlbumExistFlag(false)
                    else dataStore.saveAlbumExistFlag(true)
                    val userInfoBody = JSONObject(userInfoResponse.body()?.string())
                    dataStore.saveMemberId(userInfoBody.get("memberId").toString().toInt())
                    dataStore.saveAlbumId(userInfoBody.get("albumId").toString().toInt())

                    val intent = Intent(this@SplashActivity, GroundActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 처리 실패 시의 로직
                    Log.d("API call failed", "Nickname: ${nicknameResponse.errorBody()?.string()}, Album Exist: ${albumExistResponse.errorBody()?.string()}")
                    val intent = Intent(this@SplashActivity, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            Log.d("here3", "here3")
        }, 2000)
        viewModel.checkFirstFlag()
        viewModel.getExistFlag()
    }
}