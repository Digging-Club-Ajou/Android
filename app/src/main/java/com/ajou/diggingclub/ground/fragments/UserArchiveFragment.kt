package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentUserArchiveBinding
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.FollowingApi
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserArchiveFragment : Fragment() {
    private var _binding : FragmentUserArchiveBinding ?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val args : UserArchiveFragmentArgs by navArgs()
    private val client = RetrofitInstance.getInstance().create(FollowingApi::class.java)

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
        _binding = FragmentUserArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }

        binding.moveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_userArchiveFragment_to_likeMelodyFragment)
        }
        val albumInfo = args.albumInfo // TODO 우선은 페이지 넘겨받아서 args에 값이 있는데 이렇게 넘겨받지 않는 경우에는?
        val jsonObject = JsonObject().apply {
            addProperty("memberId", albumInfo.memberId)
        }
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())

        binding.followingBtn.text = "팔로우" // TODO 팔로우하고 있는 상태인지 받아서 초기에 텍스트 값과 selected 여부 넣어줘야함
        binding.profileIcon.setColorFilter(resources.getColor(R.color.textColor), PorterDuff.Mode.SRC_IN)
        binding.followingBtn.setOnSingleClickListener {
            if(binding.followingBtn.isSelected){
                binding.followingBtn.isSelected = false
                binding.followingBtn.text = "팔로우"
//                client.deleteFollowing()
            }else{
                binding.followingBtn.isSelected = true
                binding.followingBtn.text = "팔로잉"
                client.postFollowing(accessToken!!,refreshToken!!,requestBody).enqueue(object :
                    Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful){
                            Log.d("팔로잉 성공", "팔로잉 성공")
                        }else{
                            Log.d("팔로잉 실패",response.errorBody().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("fail",t.message.toString())
                    }
                })
            }
        }
        Glide.with(mContext!!)
            .load(albumInfo.imageUrl)
            .into(binding.image)
        binding.profile.text = albumInfo.nickname
        binding.nickname.text = albumInfo.nickname
        binding.nicknameTitle.text = albumInfo.nickname
        binding.nicknameLike.text = albumInfo.nickname
        binding.title.text = albumInfo.albumName

    }
}