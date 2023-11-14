package com.ajou.diggingclub.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.ajou.diggingclub.auth.KakaoSignUpActivity
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ActivityLandingBinding
import com.ajou.diggingclub.utils.setOnSingleClickListener

class LandingActivity : AppCompatActivity() {
    private var _binding : ActivityLandingBinding ?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        _binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val viewPagerAdapter = StartViewPagerAdapter(this)

        viewPager.adapter = viewPagerAdapter
        binding.dotsIndicator.attachTo(viewPager)

        binding.skip.setOnSingleClickListener {
            viewPager.currentItem = 3
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == 3){
                    binding.skip.visibility = View.GONE
                }else {
                    binding.skip.visibility = View.VISIBLE
                }
            }
        })
        binding.button.setOnSingleClickListener {
            val intent = Intent(this, KakaoSignUpActivity::class.java)
            startActivity(intent)
        }
    }
}