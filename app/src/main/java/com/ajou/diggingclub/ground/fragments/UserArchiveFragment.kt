package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentUserArchiveBinding
import com.ajou.diggingclub.ground.RealTimeChangeViewModel
import com.ajou.diggingclub.ground.models.FollowingModel
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.api.FollowingService
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.multiOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
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
    private val viewModel : RealTimeChangeViewModel by viewModels()
    private val TAG = UserArchiveFragment::class.java.simpleName

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
        var numOfFollower = 0

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
//                Log.d("albumInfo",albumInfo.toString())
            }else {
//                Log.d("albumId check",args.albumId.toString())
                albumService.getAlbum(accessToken!!, refreshToken!!, args.albumId)
                    .enqueue(object : Callback<ReceivedAlbumModel> {
                        override fun onResponse(
                            call: Call<ReceivedAlbumModel>,
                            response: Response<ReceivedAlbumModel>
                        ) {
                            if (response.isSuccessful) {
                                albumInfo = response.body()
//                                Log.d("albumInfo",albumInfo.toString())
                            } else {
//                                Log.d("error ", response.errorBody()?.string().toString())
                            }
                        }

                        override fun onFailure(call: Call<ReceivedAlbumModel>, t: Throwable) {
//                            Log.d("fail", t.message.toString())
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
                        if(body.get("isFollowing") == true){
                            binding.followingBtn.isSelected = true
                            binding.followingBtn.text = "팔로잉"
                            binding.melodyMove.visibility = View.VISIBLE
                            binding.notFollowingText.visibility = View.GONE
                        }else{
                            if(userId.toString() == args.memberId){
//                                Log.d("userId is ","same")
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
                        binding.followingBtn.visibility = View.VISIBLE
                    }
                }else{
//                    Log.d("followingStatus error",followingStatusResponse.errorBody()?.string().toString())
                }
                if(numberOfFollowResponse.isSuccessful){
                    followingsList = numberOfFollowResponse.body()?.followings?.toTypedArray()
                    followersList = numberOfFollowResponse.body()?.followers?.toTypedArray()
                    numOfFollower = followersList!!.size
                }else{
//                    Log.d("numberOfFollow error",numberOfFollowResponse.errorBody()?.string().toString())
                }
            withContext(Dispatchers.Main){
//                Log.d("albumInfo",albumInfo.toString())
                if(albumInfo!!.albumId != 0){
                    Glide.with(mContext!!)
                        .load(albumInfo!!.imageUrl)
                        .apply(multiOptions)
                        .into(binding.image)
                    binding.title.text = albumInfo!!.albumName
                }else{
                    Glide.with(mContext!!)
                        .clear(binding.image)
                    binding.title.text = "앨범이 없습니다"
                }
                binding.profile.text = albumInfo!!.nickname
                binding.nickname.text = albumInfo!!.nickname
                binding.nicknameTitle.text = String.format(resources.getString(R.string.melody_nickname),albumInfo!!.nickname)
                binding.nicknameLike.text = albumInfo!!.nickname
                binding.follow.text = String.format(resources.getString(R.string.follow),followersList?.toList()?.size.toString(),followingsList?.toList()?.size.toString())
                binding.follow.text = setBoldType(followersList?.toList()?.size.toString(),followingsList?.toList()?.size.toString(), binding.follow.text.toString())
            }
        }
        binding.follow.setOnClickListener{
            val action = UserArchiveFragmentDirections.actionUserArchiveFragmentToFollowingListFragment(followingsList!!,followersList!!,albumInfo!!.nickname,albumInfo!!.memberId.toString())
            findNavController().navigate(action)
        }

        binding.melodyMove.setOnClickListener {
            val action = UserArchiveFragmentDirections.actionUserArchiveFragmentToLikeMelodyFragment(albumInfo!!.nickname,albumInfo!!.memberId.toString())
            findNavController().navigate(action)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.profileIcon.setColorFilter(resources.getColor(R.color.textColor), PorterDuff.Mode.SRC_IN)

        viewModel.isFollowing.observe(viewLifecycleOwner, Observer {
            if(viewModel.isFollowing.value == true) {
                binding.melodyMove.visibility = View.VISIBLE
                binding.notFollowingText.visibility = View.GONE
                numOfFollower +=1
                binding.follow.text = String.format(resources.getString(R.string.follow),numOfFollower.toString(),followingsList?.toList()?.size.toString())
                binding.follow.text = setBoldType(numOfFollower.toString(),followingsList?.toList()?.size.toString(), binding.follow.text.toString())
            }
            else {
                binding.melodyMove.visibility = View.GONE
                binding.notFollowingText.visibility = View.VISIBLE
                numOfFollower -=1
                binding.follow.text = String.format(resources.getString(R.string.follow),numOfFollower.toString(),followingsList?.toList()?.size.toString())
                binding.follow.text = setBoldType(numOfFollower.toString(),followingsList?.toList()?.size.toString(), binding.follow.text.toString())
            }
        })
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
//                            Log.d("팔로잉 삭제 성공",response.body().toString())
                            viewModel.setFollowing(false)
//                            Log.d("numOfFollower",numOfFollower.toString())
                        }else{
//                            Log.d("팔로잉 삭제 실패 error ",response.errorBody()?.string().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        Log.d("팔로잉 삭제 실패 fail",t.message.toString())
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
//                            Log.d("팔로잉 성공", "팔로잉 성공")
                            viewModel.setFollowing(true)
//                            Log.d("numOfFollower",numOfFollower.toString())
                        }else{
//                            Log.d("팔로잉 실패",response.errorBody().toString())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        Log.d("fail",t.message.toString())
                    }
                })
            }
        }

        binding.image.setOnSingleClickListener {
            if(args.albumId != "0"){
                val action = UserArchiveFragmentDirections.actionUserArchiveFragmentToAlbumMelodyCardFragment(args.memberId,args.albumId,albumInfo!!.albumName,"others")
                findNavController().navigate(action)
            }
        }
    }

    private fun setBoldType(followerSize : String, followingSize : String, text : String) : SpannableString{
        val spannableString = SpannableString(text)

        val followerIndex = binding.follow.text.indexOf(followerSize)
        val followingIndex = binding.follow.text.lastIndexOf(followingSize)

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            followerIndex,
            followerIndex + followerSize.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            followingIndex,
            followingIndex + followingSize.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }
}