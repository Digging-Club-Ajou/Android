package com.ajou.diggingclub.melody

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ActivityMelodyBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.start.StartViewModel
import com.ajou.diggingclub.utils.setOnSingleClickListener

class MelodyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMelodyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMelodyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabProfile.setOnSingleClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.tabGround.setOnSingleClickListener {
            val intent = Intent(this, GroundActivity::class.java)
            startActivity(intent)
        }

    }
}