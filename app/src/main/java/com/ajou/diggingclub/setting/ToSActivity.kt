package com.ajou.diggingclub.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ActivityTosBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.melody.MelodyActivity
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.utils.IndentLeadingMarginSpan

class ToSActivity : AppCompatActivity() {
    private lateinit var binding : ActivityTosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        val originalText2 = getString(R.string.tos_community_guideline)
        binding.text2.text = SpannableStringBuilder(originalText2).apply {
            setSpan(IndentLeadingMarginSpan(), 0, length, 0)
        }

        val originalText5 = getString(R.string.tos_service_provider)
        binding.text5.text = SpannableStringBuilder(originalText5).apply {
            setSpan(IndentLeadingMarginSpan(), 0, length, 0)
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