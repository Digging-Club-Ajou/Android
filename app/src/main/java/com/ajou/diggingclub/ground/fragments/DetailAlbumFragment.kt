package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentDetailAlbumBinding
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.setOnSingleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailAlbumFragment : Fragment() {
    private var _binding : FragmentDetailAlbumBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null

        binding.profileIcon.setImageResource(R.color.black)

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }

        binding.followingBtn.text = "팔로우" // TODO 팔로우하고 있는 상태인지 받아서 초기에 텍스트 값과 selected 여부 넣어줘야함

        binding.followingBtn.setOnSingleClickListener {
            if(binding.followingBtn.text == "팔로우"){
                binding.followingBtn.text = "팔로잉"
                binding.followingBtn.setBackgroundColor(resources.getColor(R.color.inactiveColor))
            }else{
                binding.followingBtn.text = "팔롱우"
                binding.followingBtn.setBackgroundColor(resources.getColor(R.color.primaryColor))
            }
        }
//        binding.nickname.text = "nickname" // TODO 팔로잉한 앨범 리스트에서 선택한 하나의 앨범의 nickname 넣어주기
//        val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext,list,"recommend")
//        binding.cardRV.adapter = albumListRVAdapter
//        binding.cardRV.layoutManager = LinearLayoutManager(mContext)
    }
}