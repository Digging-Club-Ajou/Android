package com.ajou.diggingclub.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.ajou.diggingclub.databinding.ActivityProfileBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.melody.MelodyActivity
import com.ajou.diggingclub.profile.fragments.AlbumMelodyCardFragment
import com.ajou.diggingclub.profile.fragments.MyArchiveFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private val viewModel: MyAlbumViewModel by viewModels()
    private lateinit var binding : ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra("albumInfo")) {
            val receivedData = intent.getParcelableExtra<ReceivedAlbumModel>("albumInfo")
            viewModel.setAlbumInfo(receivedData!!)
            Log.d("viewModel setAlbumInfo", receivedData.toString())
        } else if (intent.hasExtra("albumId")) {
            val albumId = intent.getStringExtra("albumId")
            viewModel.setAlbumId(albumId!!)
            Log.d("viewModel setAlbumId", albumId)
        }
        if(intent.hasExtra("type")){
            val bundle = Bundle().apply {
                putString("type", intent.getStringExtra("type"))
                putString("albumId", intent.getStringExtra("albumId"))
                putString("memberId", intent.getStringExtra("memberId"))
            }
            val myArchiveFragment = MyArchiveFragment()
            myArchiveFragment.arguments = bundle
            viewModel.setMoveToMelody(true)
        }

        binding.tabGround.setOnClickListener {
            val intent = Intent(this, GroundActivity::class.java)
            startActivity(intent)
        }
        binding.tabMelody.setOnClickListener {
            val intent = Intent(this, MelodyActivity::class.java)
            startActivity(intent)
        }
        binding.tabProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}