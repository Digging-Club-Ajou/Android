package com.ajou.diggingclub.profile.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentFollowingListBinding
import com.ajou.diggingclub.ground.FollowDataViewModel
import com.ajou.diggingclub.ground.adapter.FollowListViewPagerAdapter
import com.ajou.diggingclub.ground.models.FollowingModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.start.LandingActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*

class FollowingListFragment : Fragment() {
    private var _binding : FragmentFollowingListBinding?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)
    private lateinit var viewModel : FollowDataViewModel

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
        _binding = FragmentFollowingListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[FollowDataViewModel::class.java]
        val dataStore = UserDataStore()
        var accessToken: String? = null
        var refreshToken: String? = null
        var nickname : String? = null
        val args : FollowingListFragmentArgs by navArgs()
        var userId : String ?= null
        val followingsList : List<FollowingModel> = args.followings.toList()
        val followersList : List<FollowingModel> = args.followers.toList()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            userId = dataStore.getMemberId().toString()
            nickname = dataStore.getNickname().toString()
            binding.userNickname.text = String.format(resources.getString(R.string.nickname),nickname)
            if (accessToken == null || refreshToken == null) {
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
           followingsList.map { item ->
               async {
                   if(item.albumId!=null){
                       val response = albumService.getAlbum(
                           accessToken!!, refreshToken!!, item.albumId
                       ).execute()
                       item.imageUrl = response.body()?.imageUrl.toString()
                   }
               }
           }.awaitAll()
            followersList.map { item ->
                async {
                    if(item.albumId != null){
                        val response = albumService.getAlbum(
                            accessToken!!, refreshToken!!, item.albumId
                        ).execute()
                        item.imageUrl = response.body()?.imageUrl.toString()
                    }
                }
            }.awaitAll()
            withContext(Dispatchers.Main){
                viewModel.setFollowingList(followingsList)
                viewModel.setFollowerList(followersList)
                viewModel.setMemberId(userId!!)
            }
        }
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("팔로잉"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("팔로워"))

        binding.list.adapter = FollowListViewPagerAdapter(requireActivity() as ProfileActivity)

        TabLayoutMediator(binding.tabLayout, binding.list){ tab, position ->
            tab.text = if (position == 0) "팔로잉" else "팔로워"
        }.attach()

    }
}