package com.ajou.diggingclub.profile.fragments

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentMyArchiveBinding
import com.ajou.diggingclub.ground.models.FollowingModel
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.api.FollowingService
import com.ajou.diggingclub.profile.MyAlbumViewModel
import com.ajou.diggingclub.start.LandingActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyArchiveFragment : Fragment() {
    private var _binding : FragmentMyArchiveBinding ?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val followingService = RetrofitInstance.getInstance().create(FollowingService::class.java)
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)
    private lateinit var viewModel : MyAlbumViewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    // TODO 뒤로가기 처리 제대로 안하면 args에 뭐가 없어서 앱 터짐

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[MyAlbumViewModel::class.java]
        _binding = FragmentMyArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null
        var albumInfo = viewModel.albumInfo.value
        var albumId = viewModel.albumId.value
        var followingsList : Array<FollowingModel> ?= null
        var followersList : Array<FollowingModel> ?= null
        var userId : String ?= null

        val multiOptions = MultiTransformation(
            CenterCrop(),
            RoundedCorners(40)
        ) // glide 옵션
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            userId = dataStore.getMemberId().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            if(albumInfo==null){
                Log.d("albumId",albumId.toString())
                if(albumId != 0.toString()){
                    albumService.getAlbum(accessToken!!, refreshToken!!, albumId!!)
                        .enqueue(object : Callback<ReceivedAlbumModel> {
                            override fun onResponse(
                                call: Call<ReceivedAlbumModel>,
                                response: Response<ReceivedAlbumModel>
                            ) {
                                if (response.isSuccessful) {
                                    viewModel.setAlbumInfo(response.body()!!)
                                } else {
                                    Log.d("error ", response.errorBody()?.string().toString())
                                }
                            }

                            override fun onFailure(call: Call<ReceivedAlbumModel>, t: Throwable) {
                                Log.d("fail", t.message.toString())
                            }
                        })
                }else{
                    // TODO 앨범이 없을 경우 기본 이미지 ? 를 띄우라고 하셨는데 디자인팀이랑 얘기해보고 채우기
                }
            }
            val numberOfFollowDeferred =
                async { followingService.getFollowingList(accessToken!!, refreshToken!!, userId!!) }
                val numberOfFollowResponse = numberOfFollowDeferred.await()
                if(numberOfFollowResponse.isSuccessful){
                    followingsList = numberOfFollowResponse.body()?.followings?.toTypedArray()
                    followersList = numberOfFollowResponse.body()?.followers?.toTypedArray()
                }else{
                    Log.d("numberOfFollow error",numberOfFollowResponse.errorBody()?.string().toString())
                }
            withContext(Dispatchers.Main){
                if(albumId != 0.toString()){
                    Glide.with(mContext!!)
                        .load(viewModel.albumInfo.value!!.imageUrl)
                        .apply(RequestOptions.bitmapTransform(multiOptions))
                        .into(binding.image)
                }
                binding.profile.text = viewModel.albumInfo.value!!.nickname
                binding.nickname.text = viewModel.albumInfo.value!!.nickname
                binding.nicknameTitle.text = String.format(resources.getString(R.string.melody_nickname),viewModel.albumInfo.value!!.nickname)
                binding.nicknameLike.text = viewModel.albumInfo.value!!.nickname
                binding.title.text = viewModel.albumInfo.value!!.albumName
                binding.follower.text = followersList!!.size.toString()
                binding.following.text = followingsList!!.size.toString()
            }
        }
        binding.settingBtn.setOnClickListener {
            // TODO 세팅 2.3.0 페이지로 이동
        }

        binding.editBtn.setOnClickListener {
            findNavController().navigate(R.id.action_myArchiveFragment_to_editAlbumFragment)
        }

        binding.following.setOnClickListener{
            val action = MyArchiveFragmentDirections.actionMyArchiveFragmentToFollowingListFragment(followingsList!!,followersList!!)
            findNavController().navigate(action)
        }
        binding.image.setOnClickListener {
            // TODO 2.1.2.1 페이지로 이동
        }

//
//        binding.moveBtn.setOnClickListener {
//            val action = UserArchiveFragmentDirections.actionUserArchiveFragmentToLikeMelodyFragment(viewModel.albumInfo.value!!.nickname,viewModel.albumInfo.value!!.memberId.toString())
//            findNavController().navigate(action)
//        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.profileIcon.setColorFilter(resources.getColor(R.color.textColor), PorterDuff.Mode.SRC_IN)
    }
}