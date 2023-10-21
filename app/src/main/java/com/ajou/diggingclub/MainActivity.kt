package com.ajou.diggingclub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavGraphNavigator
import androidx.navigation.findNavController
import com.ajou.diggingclub.databinding.ActivityMainBinding
import com.ajou.diggingclub.melody.CameraFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomTab.itemIconTintList = null



    }

}