package com.ajou.diggingclub.start

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airbnb.lottie.LottieAnimationView
import com.ajou.diggingclub.MainActivity
import com.ajou.diggingclub.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions

class SplashActivity : AppCompatActivity() {

    private val viewModel : StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView : ImageView = findViewById(R.id.imageView2)
        val video : VideoView = findViewById(R.id.video)
        val loadingText = findViewById<TextView>(R.id.loadingText)
        val diggleText = findViewById<TextView>(R.id.diggle)
        val description = findViewById<TextView>(R.id.text)

        val videoPath =
            "android.resource://" + packageName + "/" + R.raw.splashloading
        val uri = Uri.parse(videoPath)
        video.setVideoURI(uri)

        video.setOnCompletionListener { mediaPlayer ->
            video.stopPlayback()
        }

        video.start()  // video 관련 코드

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

           //  일정 시간 후에 다음 화면으로 이동
            Handler().postDelayed({
                if(viewModel.first.value == true){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                }else{
                    val intent = Intent(this, LandingActivity::class.java)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                }

            }, 2000)
        }, 2000)

        viewModel.checkFirstFlag()

    }
}