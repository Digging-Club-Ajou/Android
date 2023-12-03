package com.ajou.diggingclub.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ActivityGuideLineBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.melody.MelodyActivity
import com.ajou.diggingclub.profile.ProfileActivity

class GuideLineActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGuideLineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.tabGround.setOnClickListener {
            finish()
            val intent = Intent(this, GroundActivity::class.java)
            startActivity(intent)
        }
        binding.tabMelody.setOnClickListener {
            finish()
            val intent = Intent(this, MelodyActivity::class.java)
            startActivity(intent)
        }
        binding.tabProfile.setOnClickListener {
            finish()
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}