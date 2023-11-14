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
import com.ajou.diggingclub.network.api.AlbumApi
import com.ajou.diggingclub.network.api.CardApi
import com.ajou.diggingclub.network.models.FollwingAlbumResponse
import com.ajou.diggingclub.network.models.MelodyCardResponse
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.setOnSingleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroundFragment : Fragment() {

    private var _binding: FragmentGroundBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val cardApiClient = RetrofitInstance.getInstance().create(CardApi::class.java)
    private val albumApiClient = RetrofitInstance.getInstance().create(AlbumApi::class.java)

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
        var accessToken : String? = null
        var refreshToken : String? = null
        var nickname : String ?= null

        binding.imageView10.setOnSingleClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_userArchiveFragment)
        }
        binding.search.setOnSingleClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_searchUserFragment)
        }
        binding.notification.setOnSingleClickListener {
//            findNavController().navigate(R.id.action_groundFragment_to_notificationFragment)
            findNavController().navigate(R.id.action_groundFragment_to_followingListFragment)
        }
        binding.followingMore.setOnClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_albumListFragment)
        }
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            nickname = dataStore.getNickname().toString()
            binding.name.text = "'"+nickname+"'"
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            cardApiClient.getAllCards(accessToken!!,refreshToken!!).enqueue(object :Callback<MelodyCardResponse>{
                override fun onResponse(
                    call: Call<MelodyCardResponse>,
                    response: Response<MelodyCardResponse>
                ) {
                    if(response.isSuccessful){
                        if(response.headers()["AccessToken"] != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                            }
                        }
                        val cardList : List<ReceivedMelodyCardModel> = response.body()!!.melodyCardListResult
                        Log.d("success",cardList.toString())

                        val cardViewPagerAdapter = MelodyCardRVAdapter(mContext!!,cardList)
                        binding.cardRV.adapter = cardViewPagerAdapter
                        binding.cardRV.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    }else {
                        Log.d("response not successful",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<MelodyCardResponse>, t: Throwable) {
                    Log.d("failll",t.message.toString())
                }
            })
            albumApiClient.getFollowingAlbums(accessToken!!,refreshToken!!).enqueue(object :Callback<FollwingAlbumResponse>{
                override fun onResponse(
                    call: Call<FollwingAlbumResponse>,
                    response: Response<FollwingAlbumResponse>
                ) {
                    if(response.isSuccessful){
                        if(response.headers()["AccessToken"] != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                            }
                        }
                        val list : List<ReceivedAlbumModel> = response.body()!!.albumListResult
                        Log.d("success",list.toString())
                        val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext!!,list,"following")
                        binding.followingRV.adapter = albumListRVAdapter
                        binding.followingRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
                    }else{
                        Log.d("response not successful",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<FollwingAlbumResponse>, t: Throwable) {
                    Log.d("failll",t.message.toString())
                }
            })
        }


//
//        albumApiClient.getFollowingAlbums(accessToken!!,refreshToken!!).enqueue(object :Callback<FollwingAlbumResponse>{
//            override fun onResponse(
//                call: Call<FollwingAlbumResponse>,
//                response: Response<FollwingAlbumResponse>
//            ) {
//                if(response.isSuccessful){
//                    if(response.headers()["AccessToken"] != null) {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
//                        }
//                    }
//                    val list : List<ReceivedAlbumModel> = response.body()!!.albumListResult
//                    Log.d("success",list.toString())
//                    val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext,list,"recommend")
//                    binding.aiRecommendRV.adapter = albumListRVAdapter
//                    binding.aiRecommendRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
//                    binding.recommendRV.adapter = albumListRVAdapter
//                    binding.recommendRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
//                }else{
//                    Log.d("response not successful",response.errorBody()?.string().toString())
//                }
//            }
//
//            override fun onFailure(call: Call<FollwingAlbumResponse>, t: Throwable) {
//                Log.d("failll",t.message.toString())
//            }
//        })
        // 추후에 추천앨범 2가지 api로 변경하기
    }
}