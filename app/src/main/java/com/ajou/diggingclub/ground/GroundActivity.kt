package com.ajou.diggingclub.ground

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ActivityGroundBinding
import com.ajou.diggingclub.melody.MelodyActivity
import com.ajou.diggingclub.profile.ProfileActivity

class GroundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.tabMelody.setOnClickListener {
            val intent = Intent(this, MelodyActivity::class.java)
            startActivity(intent)
        }
    }
}