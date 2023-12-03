package com.ajou.diggingclub.ground

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ActivityGroundBinding
import com.ajou.diggingclub.melody.MelodyActivity
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.utils.setOnSingleClickListener

class GroundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroundBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabProfile.setOnSingleClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.tabMelody.setOnSingleClickListener {
            val intent = Intent(this, MelodyActivity::class.java)
            startActivity(intent)
        }
        binding.tabGround.setOnSingleClickListener {
            val intent = Intent(this, GroundActivity::class.java)
            startActivity(intent)
        }
    }

}