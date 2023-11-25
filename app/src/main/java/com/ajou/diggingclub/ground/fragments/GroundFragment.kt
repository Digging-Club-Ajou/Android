package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentGroundBinding
import com.ajou.diggingclub.ground.adapter.FollowingAlbumListRVAdapter
import com.ajou.diggingclub.ground.adapter.MelodyCardRVAdapter
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.api.CardService
import com.ajou.diggingclub.network.api.FavoriteService
import com.ajou.diggingclub.network.api.MusicService
import com.ajou.diggingclub.network.models.AlbumResponse
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.AdapterToFragment
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroundFragment : Fragment(), AdapterToFragment {

    private var _binding: FragmentGroundBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val cardServiceClient = RetrofitInstance.getInstance().create(CardService::class.java)
    private val albumServiceClient = RetrofitInstance.getInstance().create(AlbumService::class.java)
    private val favoriteService = RetrofitInstance.getInstance().create(FavoriteService::class.java)
    private val musicServiceClient = RetrofitInstance.getInstance().create(MusicService::class.java)

    var accessToken : String? = null
    var refreshToken : String? = null
    private lateinit var cardViewPagerAdapter : MelodyCardRVAdapter
    var cardList : ArrayList<ReceivedMelodyCardModel> = arrayListOf()
    var userId : String ?= null

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
        _binding = FragmentGroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var nickname : String ?= null
//        var albumId : String ?= null

        binding.search.setOnSingleClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_searchUserFragment)
        }
        binding.notification.setOnSingleClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_notificationFragment)
        }
        binding.followingMore.setOnClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_albumListFragment)
        }

        var previousPosition = binding.cardRV.currentItem
        binding.cardRV.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position != previousPosition && cardList[previousPosition].isPlaying){
                    cardViewPagerAdapter.stopMediaPlayer(previousPosition)
                    cardList[previousPosition].isPlaying = false
                    binding.cardRV.post {
                        cardViewPagerAdapter.notifyItemChanged(previousPosition)
                        previousPosition = position
                    }
                }else{
                    previousPosition = position
                }
            }
        })
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            nickname = dataStore.getNickname().toString()
            userId = dataStore.getMemberId().toString()
            if(dataStore.getAlbumExistFlag()){
                Log.d("album Exist flag",dataStore.getAlbumExistFlag().toString())
            }
//            albumId = dataStore.getAlbumId().toString()
            binding.name.text = "'"+nickname+"'"
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
//            val cardResponse = cardApiClient.getAlbumCards(accessToken!!, refreshToken!!, albumId!!)
//            if(cardResponse.isSuccessful){
//                Log.d("accessToken",accessToken.toString())
//                if(cardResponse.headers()["AccessToken"] != null) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        dataStore.saveAccessToken(cardResponse.headers()["AccessToken"].toString())
//                    }
//                }
//                cardList = cardResponse.body()!!.melodyCardListResult
//                Log.d("cardList",cardList.toString())
//                cardViewPagerAdapter = MelodyCardRVAdapter(mContext!!,cardList,this@GroundFragment, "viewpager")
//                binding.cardRV.adapter = cardViewPagerAdapter
//                binding.cardRV.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//            } else {
//                Log.d("response not successful",cardResponse.errorBody()?.string().toString())
//            }
            albumServiceClient.getFollowingAlbums(accessToken!!,refreshToken!!).enqueue(object :Callback<AlbumResponse>{
                override fun onResponse(
                    call: Call<AlbumResponse>,
                    response: Response<AlbumResponse>
                ) {
                    if(response.isSuccessful){
                        val list : List<ReceivedAlbumModel> = response.body()!!.albumListResult
                        Log.d("list",list.toString())
                        val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext!!,list,"following",this@GroundFragment)
                        binding.followingRV.adapter = albumListRVAdapter
                        binding.followingRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)

                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in list.indices){
                                val response =  cardServiceClient.getAlbumCards(accessToken!!, refreshToken!!, list[i].albumId.toString())
                                if(response.isSuccessful){
                                    Log.d("response cardList",response.body()!!.melodyCardListResult.toString())
                                    cardList.addAll(response.body()!!.melodyCardListResult)
                                    Log.d("success cardList",cardList.toString())
                                }else{
                                    Log.d("response not successful",response.errorBody()?.string().toString())
                                }
                            }
                            withContext(Dispatchers.Main){
                                Log.d("cardList url check ",cardList.toString())
                                cardViewPagerAdapter = MelodyCardRVAdapter(mContext!!,cardList,this@GroundFragment, "viewpager")
                                binding.cardRV.adapter = cardViewPagerAdapter
                                binding.cardRV.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                            }
                        }


                    }else{
                        Log.d("FollowingAlbum",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                    Log.d("failll",t.message.toString())
                }
            })
            albumServiceClient.getFollowingAlbums(accessToken!!,refreshToken!!).enqueue(object :Callback<AlbumResponse>{
                override fun onResponse(
                    call: Call<AlbumResponse>,
                    response: Response<AlbumResponse>
                ) {
                    if(response.isSuccessful){
                        if(response.headers()["AccessToken"] != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                            }
                        }
                        val list : List<ReceivedAlbumModel> = response.body()!!.albumListResult
                        Log.d("success",list.toString())
                        val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext!!,list,"recommend",this@GroundFragment)
                        binding.aiRecommendRV.adapter = albumListRVAdapter
                        binding.aiRecommendRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
                        binding.recommendRV.adapter = albumListRVAdapter
                        binding.recommendRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
                    }else{
                        Log.d("response not successful",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                    Log.d("failll",t.message.toString())
                }
            })
            // 추후에 추천앨범 2가지 api로 변경하기
        }
    }

    override fun getSelectedId(memberId: String, albumId: String, name : String, type: String) {
        when(type){
            "card" -> {
                if(userId == memberId){
                    val intent = Intent(mContext, ProfileActivity::class.java)
                    intent.putExtra("albumId",albumId)
                    startActivity(intent)
                }
                val action = GroundFragmentDirections.actionGroundFragmentToUserArchiveFragment(null,albumId,memberId)
                findNavController().navigate(action)
            }
            "album" -> {
                val bottomSheetDialogFragment = BottomSheetFragment()
                val bundle = Bundle().apply {
                    putString("albumId",albumId)
                    putString("memberId",memberId)
                    putString("name",name)
                }
                bottomSheetDialogFragment.arguments = bundle
                bottomSheetDialogFragment.show(parentFragmentManager,bottomSheetDialogFragment.tag)
            }
            else -> {
                Log.d("else","error....")
            }
        }
    }

    override fun getSelectedItem(item: ReceivedAlbumModel) {
        if(userId == item.memberId.toString()){
            val intent = Intent(mContext, ProfileActivity::class.java)
            intent.putExtra("albumInfo",item)
            // TODO 내 앨범인 경우이므로 intent 이동해야함
        }
        val action = GroundFragmentDirections.actionGroundFragmentToUserArchiveFragment(item,item.albumId.toString(),item.memberId.toString())
        findNavController().navigate(action)
    }

    override fun postFavoriteId(melodyCardId: String, isLike: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            if(isLike){
                val response = favoriteService.postFavorite(accessToken!!,refreshToken!!,melodyCardId)
                if(response.isSuccessful){
                    Log.d("success add",response.body().toString())
                }else{
                    Log.d("error ",response.errorBody()?.string().toString())
                    // TODO 실패했으면 하트 눌린 거 취소되어야 할 듯?
                }
            }else{
                val response = favoriteService.deleteFavorite(accessToken!!,refreshToken!!,melodyCardId)
                if(response.isSuccessful){
                    Log.d("success delete",response.body().toString())
                }else{
                    Log.d("error",response.errorBody()?.string().toString())
                }
            }
        }
    }

    override fun addRecordCount(position : Int) {
        val jsonObject = JsonObject().apply {
            addProperty("artistName",cardList[position].artistName)
            addProperty("songTitle", cardList[position].songTitle)
        }
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
        musicServiceClient.addCount(accessToken!!,refreshToken!!,requestBody).enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    Log.d("success",response.body().toString())
                }else{
                    Log.d("error",response.errorBody()?.string().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("fail",t.message.toString())
            }

        })
    }
}