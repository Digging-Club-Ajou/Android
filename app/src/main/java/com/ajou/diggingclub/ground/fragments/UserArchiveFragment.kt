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
import com.ajou.diggingclub.ground.models.FollowingModel
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.api.FollowingService
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.multiOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserArchiveFragment : Fragment() {
    private var _binding : FragmentUserArchiveBinding ?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val args : UserArchiveFragmentArgs by navArgs()
    private val followingService = RetrofitInstance.getInstance().create(FollowingService::class.java)
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)

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
        _binding = FragmentUserArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null
        var albumInfo : ReceivedAlbumModel? = null
        var followingsList : Array<FollowingModel> ?= null
        var followersList : Array<FollowingModel> ?= null
        var userId : String ?= null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            userId = dataStore.getMemberId().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            if(args.albumInfo!=null){
                albumInfo = args.albumInfo
            }else {
                albumService.getAlbum(accessToken!!, refreshToken!!, args.albumId)
                    .enqueue(object : Callback<ReceivedAlbumModel> {
                        override fun onResponse(
                            call: Call<ReceivedAlbumModel>,
                            response: Response<ReceivedAlbumModel>
                        ) {
                            if (response.isSuccessful) {
                                albumInfo = response.body()
                            } else {
                                Log.d("error ", response.errorBody()?.string().toString())
                            }
                        }

                        override fun onFailure(call: Call<ReceivedAlbumModel>, t: Throwable) {
                            Log.d("fail", t.message.toString())
                        }
                    })
            }
            val followingStatusDeferred = async {
                followingService.getFollowingStatus(
                    accessToken!!,
                    refreshToken!!,
                    args.memberId
                )
            }
            val numberOfFollowDeferred =
                async { followingService.getFollowingList(accessToken!!, refreshToken!!, args.memberId) }
                val followingStatusResponse = followingStatusDeferred.await()
                val numberOfFollowResponse = numberOfFollowDeferred.await()

                if(followingStatusResponse.isSuccessful){
                    val body = JSONObject(followingStatusResponse.body()?.string())
                    withContext(Dispatchers.Main){
                        Log.d("userId not same",userId.toString())
                        Log.d("memberId",args.memberId)
                        if(body.get("isFollowing") == true){
                            binding.followingBtn.isSelected = true
                            binding.followingBtn.text = "팔로잉"
                            binding.melodyMove.visibility = View.VISIBLE
                            binding.notFollowingText.visibility = View.GONE
                        }else{
                            Log.d("userId",userId.toString())
                            if(userId.toString() == args.memberId){
                                Log.d("userId is ","same")
                                binding.followingBtn.visibility = View.INVISIBLE
                                binding.editBtn.visibility = View.VISIBLE
                                binding.settingBtn.visibility = View.VISIBLE
                                binding.melodyMove.visibility = View.VISIBLE
                                binding.notFollowingText.visibility = View.GONE
                            }else{
                                binding.followingBtn.isSelected = false
                                binding.followingBtn.text = "팔로우"
                                binding.notFollowingText.visibility = View.VISIBLE
                                binding.melodyMove.visibility = View.GONE
                            }
                        }
                    }
                }else{
                    Log.d("followingStatus error",followingStatusResponse.errorBody()?.string().toString())
                }
                if(numberOfFollowResponse.isSuccessful){
                    followingsList = numberOfFollowResponse.body()?.followings?.toTypedArray()
                    followersList = numberOfFollowResponse.body()?.followers?.toTypedArray()
                    Log.d("followingsList",followingsList.toString())
                    Log.d("followersList",followersList.toString())
                }else{
                    Log.d("numberOfFollow error",numberOfFollowResponse.errorBody()?.string().toString())
                }
            withContext(Dispatchers.Main){
                Glide.with(mContext!!)
                    .load(albumInfo!!.imageUrl)
                    .apply(multiOptions)
                    .into(binding.image)
                binding.profile.text = albumInfo!!.nickname
                binding.nickname.text = albumInfo!!.nickname
                binding.nicknameTitle.text = String.format(resources.getString(R.string.melody_nickname),albumInfo!!.nickname)
                binding.nicknameLike.text = albumInfo!!.nickname
                binding.title.text = albumInfo!!.albumName
                binding.follower.text = followersList!!.size.toString()
                binding.following.text = followingsList!!.size.toString()
            }
        }
        binding.following.setOnClickListener{
            val action = UserArchiveFragmentDirections.actionUserArchiveFragmentToFollowingListFragment(followingsList!!,followersList!!,albumInfo!!.nickname)
            findNavController().navigate(action)
        }

        binding.moveBtn.setOnClickListener {
            val action = UserArchiveFragmentDirections.actionUserArchiveFragmentToLikeMelodyFragment(albumInfo!!.nickname,albumInfo!!.memberId.toString())
            findNavController().navigate(action)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.profileIcon.setColorFilter(resources.getColor(R.color.textColor), PorterDuff.Mode.SRC_IN)
        binding.followingBtn.setOnSingleClickListener {
            if(binding.followingBtn.isSelected){
                binding.followingBtn.isSelected = false
                binding.followingBtn.text = "팔로우"
                val jsonObject = JsonObject().apply {
                    addProperty("followingId", userId)
                    addProperty("followedId", args.memberId)
                }
                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
                followingService.deleteFollowing(accessToken!!, refreshToken!!, requestBody).enqueue(object : Callback<ResponseBody>{
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful){
                            Log.d("팔로잉 삭제 성공",response.body().toString())
                        }else{
                            Log.d("팔로잉 삭제 실패 error ",response.errorBody()?.string().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("팔로잉 삭제 실패 fail",t.message.toString())
                    }
                })
            }else{
                binding.followingBtn.isSelected = true
                binding.followingBtn.text = "팔로잉"
                val jsonObject = JsonObject().apply {
                    addProperty("memberId", args.memberId)
                }
                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
                followingService.postFollowing(accessToken!!,refreshToken!!,requestBody).enqueue(object :
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
    }
}