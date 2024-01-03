package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.lang.Math.abs

class GroundFragment : Fragment(), AdapterToFragment {

    private var _binding: FragmentGroundBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null

    private val cardService = RetrofitInstance.getInstance().create(CardService::class.java)
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)
    private val favoriteService = RetrofitInstance.getInstance().create(FavoriteService::class.java)
    private val musicService = RetrofitInstance.getInstance().create(MusicService::class.java)

    var followingAlbumList = listOf<ReceivedAlbumModel>()

    private val TAG = GroundFragment::class.java.simpleName

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
        var aiRecommendAlbumList = listOf<ReceivedAlbumModel>()
        var genreRecommendAlbumList = listOf<ReceivedAlbumModel>()

        binding.search.setOnSingleClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_searchUserFragment)
        }
        binding.notification.setOnSingleClickListener {
            findNavController().navigate(R.id.action_groundFragment_to_notificationFragment)
        }

        binding.followingMore.setOnClickListener {
            val action = GroundFragmentDirections.actionGroundFragmentToAlbumListFragment("following",followingAlbumList.toTypedArray())
            findNavController().navigate(action)
        }
        binding.followingMoreBtn.setOnClickListener {
            val action = GroundFragmentDirections.actionGroundFragmentToAlbumListFragment("following",followingAlbumList.toTypedArray())
            findNavController().navigate(action)
        }
        binding.aiRecommendMore.setOnClickListener {
            val action = GroundFragmentDirections.actionGroundFragmentToAlbumListFragment("ai",aiRecommendAlbumList.toTypedArray())
            findNavController().navigate(action)
        }
        binding.aiRecommendMoreBtn.setOnClickListener {
            val action = GroundFragmentDirections.actionGroundFragmentToAlbumListFragment("ai",aiRecommendAlbumList.toTypedArray())
            findNavController().navigate(action)
        }
        binding.genreRecommendMore.setOnClickListener {
            val action = GroundFragmentDirections.actionGroundFragmentToAlbumListFragment("genre",genreRecommendAlbumList.toTypedArray())
            findNavController().navigate(action)
        }
        binding.genreRecommendMoreBtn.setOnClickListener {
            val action = GroundFragmentDirections.actionGroundFragmentToAlbumListFragment("genre",genreRecommendAlbumList.toTypedArray())
            findNavController().navigate(action)
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
//            albumId = dataStore.getAlbumId().toString()
            binding.name.text = "'"+nickname+"'"
//            Log.d("accessToken",accessToken.toString())
//            Log.d("refreshToken",refreshToken.toString())
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            albumService.getFollowingAlbums(accessToken!!,refreshToken!!).enqueue(object :Callback<AlbumResponse>{
                override fun onResponse(
                    call: Call<AlbumResponse>,
                    response: Response<AlbumResponse>
                ) {
                    if(response.isSuccessful){
                        followingAlbumList = response.body()!!.albumListResult
//                        Log.d("list",followingAlbumList.toString())
                        val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext!!,followingAlbumList,this@GroundFragment)
                        binding.followingRV.adapter = albumListRVAdapter
                        binding.followingRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
                        binding.followingRV.addItemDecoration(FollowingAlbumListRVAdapter.HorizontalItemDecoration(8))

                        CoroutineScope(Dispatchers.IO).launch {
                            for (i in followingAlbumList.indices){
                                val response =  cardService.getAlbumCards(accessToken!!, refreshToken!!, followingAlbumList[i].albumId.toString())
                                if(response.isSuccessful){
                                    cardList.addAll(response.body()!!.melodyCardListResult)
//                                    Log.d("success cardList",cardList.toString())
                                }else{
//                                    Log.d("response not successful",response.errorBody()?.string().toString())
                                }
                            }
                            withContext(Dispatchers.Main){
                                cardViewPagerAdapter = MelodyCardRVAdapter(mContext!!,cardList,this@GroundFragment, "viewpager")
                                binding.cardRV.adapter = cardViewPagerAdapter
                                binding.cardRV.orientation = ViewPager2.ORIENTATION_HORIZONTAL

                                binding.cardRV.offscreenPageLimit = 4
                                // item_view 간의 양 옆 여백을 상쇄할 값
                                val offsetBetweenPages = resources.getDimensionPixelOffset(R.dimen.offsetBetweenPages).toFloat()
                                binding.cardRV.setPageTransformer { page, position ->
                                    val myOffset = position * -(2 * offsetBetweenPages)
                                    if (position < -1) {
                                        page.translationX = -myOffset
                                    } else if (position <= 1) {
                                        // Paging 시 Y축 Animation 배경색을 약간 연하게 처리
                                        val scaleFactor = 0.9f.coerceAtLeast(1 - kotlin.math.abs(position))
                                        page.translationX = myOffset
                                        page.scaleY = scaleFactor
                                        page.alpha = scaleFactor
                                    } else {
                                        page.alpha = 0f
                                        page.translationX = myOffset
                                    }
                                }
//                                binding.dotsIndicator.attachTo(binding.cardRV)
                            }
                        }


                    }else{
//                        Log.d("FollowingAlbum",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
//                    Log.d("failll",t.message.toString())
                }
            })
            albumService.getAIAlbums(accessToken!!,refreshToken!!).enqueue(object :Callback<AlbumResponse>{
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
                        aiRecommendAlbumList = response.body()!!.albumListResult
//                        Log.d("success",aiRecommendAlbumList.toString())
                        val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext!!,aiRecommendAlbumList,this@GroundFragment)
                        binding.aiRecommendRV.adapter = albumListRVAdapter
                        binding.aiRecommendRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
                        binding.aiRecommendRV.addItemDecoration(FollowingAlbumListRVAdapter.HorizontalItemDecoration(8))
                    }else{
//                        Log.d("response not successful",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
//                    Log.d("failll",t.message.toString())
                }
            })

            albumService.getGenreAlbums(accessToken!!,refreshToken!!).enqueue(object :Callback<AlbumResponse>{
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
                        genreRecommendAlbumList = response.body()!!.albumListResult
//                        Log.d("success",genreRecommendAlbumList.toString())
                        val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext!!,genreRecommendAlbumList,this@GroundFragment)
                        binding.recommendRV.adapter = albumListRVAdapter
                        binding.recommendRV.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
                        binding.recommendRV.addItemDecoration(FollowingAlbumListRVAdapter.HorizontalItemDecoration(8))
                    }else{
//                        Log.d("response not successful",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
//                    Log.d("failll",t.message.toString())
                }
            })
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
                if(memberId != userId){
                    val action = GroundFragmentDirections.actionGroundFragmentToAlbumMelodyCardFragment(memberId,albumId, name, "others")
                    findNavController().navigate(action)
                }else{
                    val intent = Intent(mContext, ProfileActivity::class.java)
                    intent.putExtra("toAlbum",true)
                    intent.putExtra("albumId",albumId)
                    intent.putExtra("name",name)
                    intent.putExtra("type","my")
                }
            }
            else -> {
//                Log.d("else","error....")
            }
        }
    }

    override fun getSelectedItem(item: ReceivedAlbumModel) {
        if(userId == item.memberId.toString()){
            val intent = Intent(mContext, ProfileActivity::class.java)
            intent.putExtra("albumInfo",item)
            startActivity(intent)
            // 내 앨범인 경우이므로 intent 이동해야함
        }else{
            val action = GroundFragmentDirections.actionGroundFragmentToUserArchiveFragment(item,item.albumId.toString(),item.memberId.toString())
            findNavController().navigate(action)
        }
    }

    override fun postFavoriteId(melodyCardId: String, isLike: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            if(isLike){
                val response = favoriteService.postFavorite(accessToken!!,refreshToken!!,melodyCardId)
                if(response.isSuccessful){
//                    Log.d("success add",response.body().toString())
                }else{
//                    Log.d("error ",response.errorBody()?.string().toString())
                    // TODO 실패했으면 하트 눌린 거 취소되어야 할 듯?
                }
            }else{
                val response = favoriteService.deleteFavorite(accessToken!!,refreshToken!!,melodyCardId)
                if(response.isSuccessful){
//                    Log.d("success delete",response.body().toString())
                }else{
//                    Log.d("error",response.errorBody()?.string().toString())
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
        musicService.addCount(accessToken!!,refreshToken!!,requestBody).enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
//                    Log.d("success",response.body().toString())
                }else{
//                    Log.d("error",response.errorBody()?.string().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.d("fail",t.message.toString())
            }

        })
    }

    override fun shareCard(item: ReceivedMelodyCardModel) {

    }

    override fun onPause() {
        super.onPause()
        if(::cardViewPagerAdapter.isInitialized){
            for(i in 0 until cardList.size){
                cardViewPagerAdapter.stopMediaPlayer(i)
                cardList[i].isPlaying = false
            }
        }
    }

}