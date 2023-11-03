package com.ajou.diggingclub.melody

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ActivityMelodyBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.profile.ProfileActivity

class MelodyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMelodyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMelodyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.tabGround.setOnClickListener {
            val intent = Intent(this, GroundActivity::class.java)
            startActivity(intent)
        }
    }
}