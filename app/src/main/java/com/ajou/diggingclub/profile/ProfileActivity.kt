package com.ajou.diggingclub.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.profile.fragments.MyArchiveFragment

class ProfileActivity : AppCompatActivity() {
    private lateinit var viewModel : MyAlbumViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        viewModel = ViewModelProvider(this)[MyAlbumViewModel::class.java]

        if(intent.hasExtra("albumInfo")){
            val receivedData = intent.getParcelableExtra<ReceivedAlbumModel>("albumInfo")
            viewModel.setAlbumInfo(receivedData!!)
        }else if(intent.hasExtra("albumId")) {
            val albumId = intent.getStringExtra("albumId")
            viewModel.setAlbumId(albumId!!)
        }
    }
}