package com.ajou.diggingclub.profile.fragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
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
import com.ajou.diggingclub.setting.SettingActivity
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.multiOptions
import com.bumptech.glide.Glide
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
    private val TAG = MyArchiveFragment::class.java.simpleName

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
        var nickname : String ?= null

        if(viewModel.moveToMelody.value == true){
            var argsMemberId = ""
            var argsAlbumId = ""
            var argsNickname = ""
            arguments?.let {
                argsMemberId = it.getString("memberId").toString()
                argsAlbumId = it.getString("albumId").toString()
                argsNickname = it.getString("name").toString()
            }
            viewModel.setMoveToMelody(false)
            val action = MyArchiveFragmentDirections.actionMyArchiveFragmentToAlbumMelodyCardFragment(argsMemberId,argsAlbumId,argsNickname,"my")
            findNavController().navigate(action)
        }
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            userId = dataStore.getMemberId().toString()
            nickname = dataStore.getNickname().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            if(albumInfo==null){
                Log.d("albumId",albumId.toString())
                if(albumId == null){
                    Log.d(TAG, "albumId is null")
                    albumService.getMyAlbum(accessToken!!, refreshToken!!).enqueue(object : Callback<ReceivedAlbumModel> {
                        override fun onResponse(
                            call: Call<ReceivedAlbumModel>,
                            response: Response<ReceivedAlbumModel>
                        ) {
                                if(response.isSuccessful){
                                    val body = response.body()
                                    viewModel.setAlbumInfo(body!!)
                                    viewModel.setAlbumId(body.albumId.toString())
                                    Log.d("viewmodel body",viewModel.albumInfo.value.toString())
                                    Log.d("viewmodel body",viewModel.albumId.value.toString())
                                }else{
                                    Log.d(TAG,response.errorBody()?.string().toString())
                                    viewModel.setAlbumId("0")
                                    albumId = "0"
                                    Glide.with(mContext!!)
                                        .clear(binding.image)
                                    binding.title.text = "앨범이 없습니다"
                                }
                        }
                        override fun onFailure(call: Call<ReceivedAlbumModel>, t: Throwable) {
                            Log.d(TAG, t.message.toString())
                        }
                    })
                } else if(albumId != "0"){
                    albumService.getAlbum(accessToken!!, refreshToken!!, albumId.toString())
                        .enqueue(object : Callback<ReceivedAlbumModel> {
                            override fun onResponse(
                                call: Call<ReceivedAlbumModel>,
                                response: Response<ReceivedAlbumModel>
                            ) {
                                if (response.isSuccessful) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        viewModel.setAlbumInfo(response.body()!!)
                                        viewModel.setAlbumId(viewModel.albumInfo.value!!.albumId.toString())
                                    }
                                } else {
                                    Log.d("error ", response.errorBody()?.string().toString())
                                }
                            }

                            override fun onFailure(call: Call<ReceivedAlbumModel>, t: Throwable) {
                                Log.d("fail", t.message.toString())
                            }
                        })
                }else{
                    Log.d("albumId is 0 !","")
                    viewModel.setAlbumId("0")
                    albumId = "0"
                    Glide.with(mContext!!)
                        .clear(binding.image)
                    binding.title.text = "앨범이 없습니다"
                }
            }else{
                viewModel.setAlbumId(viewModel.albumInfo.value!!.albumId.toString())
            }
            Log.d(TAG,userId.toString())
            val numberOfFollowDeferred =
                async { followingService.getFollowingList(accessToken!!, refreshToken!!,userId!!) }
                val numberOfFollowResponse = numberOfFollowDeferred.await()
                if(numberOfFollowResponse.isSuccessful){
                    followingsList = numberOfFollowResponse.body()?.followings?.toTypedArray()
                    followersList = numberOfFollowResponse.body()?.followers?.toTypedArray()
                }else{
                    Log.d("numberOfFollow error",numberOfFollowResponse.errorBody()?.string().toString())
                }
            withContext(Dispatchers.Main){
                viewModel.albumInfo.observe(requireActivity(), Observer{
                    if(viewModel.albumId.value != "0"){
                        Glide.with(mContext!!)
                            .load(viewModel.albumInfo.value!!.imageUrl)
                            .apply(multiOptions)
                            .into(binding.image)
                        binding.title.text = viewModel.albumInfo.value!!.albumName
                    }
                })
                Log.d("follwingsList",followingsList?.toList()?.size.toString())
                binding.profile.text = nickname
                binding.nickname.text = nickname
                binding.nicknameTitle.text = String.format(resources.getString(R.string.melody_nickname),nickname)
                binding.nicknameLike.text = nickname
                binding.follow.text = String.format(resources.getString(R.string.follow),followersList?.toList()?.size.toString(),followingsList?.toList()?.size.toString())
                val spannableString = SpannableString(binding.follow.text)

                val followerIndex = binding.follow.text.indexOf(followingsList?.toList()?.size.toString())
                val followingIndex = binding.follow.text.lastIndexOf(followersList?.toList()?.size.toString())

                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    followerIndex,
                    followerIndex + followersList?.toList()?.size.toString().length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    followingIndex,
                    followingIndex + followersList?.toList()?.size.toString().length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                binding.follow.text = spannableString
            }
        }
        binding.settingBtn.setOnClickListener {
            val intent = Intent(mContext, SettingActivity::class.java)
            startActivity(intent)
        }

        binding.editBtn.setOnClickListener {
            findNavController().navigate(R.id.action_myArchiveFragment_to_editAlbumFragment)
        }

        binding.follow.setOnClickListener{
            val action = MyArchiveFragmentDirections.actionMyArchiveFragmentToFollowingListFragment(followingsList!!,followersList!!)
            findNavController().navigate(action)
        }
        binding.image.setOnClickListener {
            Log.d("albumId",albumId.toString())
            if(albumId != "0"){
                val action = MyArchiveFragmentDirections.actionMyArchiveFragmentToAlbumMelodyCardFragment(userId.toString(),viewModel.albumId.value.toString(),viewModel.albumInfo.value!!.albumName,"my")
                findNavController().navigate(action)
            }
        }
        binding.melodyMove.setOnClickListener {
            val action = MyArchiveFragmentDirections.actionMyArchiveFragmentToLikeMelodyFragment(userId.toString(),nickname.toString())
            findNavController().navigate(action)
        }

        binding.profileIcon.setColorFilter(resources.getColor(R.color.textColor), PorterDuff.Mode.SRC_IN)
    }
}