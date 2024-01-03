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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentAlbumListBinding
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
import com.ajou.diggingclub.utils.fromDpToPx
import kotlinx.coroutines.*
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumListFragment : Fragment(), AdapterToFragment {
    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private val cardService = RetrofitInstance.getInstance().create(CardService::class.java)
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)
    private val favoriteService = RetrofitInstance.getInstance().create(FavoriteService::class.java)
    private val musicService = RetrofitInstance.getInstance().create(MusicService::class.java)
    private val TAG = AlbumListFragment::class.java.simpleName

    var accessToken : String? = null
    var refreshToken : String? = null
    var userId : String ?= null
    val dataStore = UserDataStore()

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
        _binding = FragmentAlbumListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var nickname: String? = null
        val args : AlbumListFragmentArgs by navArgs()
        val list = args.albums.toList()
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            nickname = dataStore.getNickname().toString()
            userId = dataStore.getMemberId().toString()
            var title = ""
            if (accessToken == null || refreshToken == null) {
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            when(args.type){
                "following" -> {
                    title = "내가 팔로우하는 앨범 살펴보기"
                }
                "ai" -> {
                    title = nickname + "님만을 위한 추천 앨범이에요!"
                }
                "genre" -> {
                    title = "오늘, 이런 장르는 어떠세요?"
                }
                else -> {
                }
            }
            withContext(Dispatchers.Main) {
                binding.name.text = title
                val albumListRVAdapter = FollowingAlbumListRVAdapter(mContext!!, list, this@AlbumListFragment)
                binding.albumRV.adapter = albumListRVAdapter
                val gridLayoutManager = GridLayoutManager(mContext, 2)
                binding.albumRV.layoutManager = gridLayoutManager
                binding.albumRV.addItemDecoration(FollowingAlbumListRVAdapter.GridSpacingItemDecoration(2, 4f.fromDpToPx()))
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun getSelectedItem(item: ReceivedAlbumModel) {
//        Log.d(TAG,"userId : $userId  item.memberId : ${item.memberId}")
        if(userId == item.memberId.toString()){
//            Log.d(TAG,"id is same")
            val intent = Intent(mContext, ProfileActivity::class.java)
            intent.putExtra("albumInfo",item)
            startActivity(intent)
        }else{
            val action = AlbumListFragmentDirections.actionAlbumListFragmentToUserArchiveFragment(item,item.albumId.toString(),item.memberId.toString())
            findNavController().navigate(action)
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
                val action = AlbumListFragmentDirections.actionAlbumListFragmentToUserArchiveFragment(null,albumId,memberId)
                findNavController().navigate(action)
            }
            "album" -> {
                if(memberId != userId){
                    val action = AlbumListFragmentDirections.actionAlbumListFragmentToAlbumMelodyCardFragment(memberId,albumId, name, "others")
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

    override fun postFavoriteId(melodyCardId: String, isLike: Boolean) {
        // TODO
    }

    override fun addRecordCount(position: Int) {
        // TODO
    }

    override fun shareCard(item: ReceivedMelodyCardModel) {

    }

    private suspend fun getFollowingAlbums() : List<ReceivedAlbumModel> {
        var list = listOf<ReceivedAlbumModel>()
        albumService.getFollowingAlbums(accessToken!!, refreshToken!!).enqueue(object :
            Callback<AlbumResponse> {
            override fun onResponse(
                call: Call<AlbumResponse>,
                response: Response<AlbumResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.headers()["AccessToken"] != null) {
                        CoroutineScope(Dispatchers.IO).launch {
//                            Log.d(
//                                "new AccessToken",
//                                response.headers()["AccessToken"].toString()
//                            )
                            dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                        }
                    }
                    list = response.body()!!.albumListResult
//                    Log.d(TAG,"following $list")
                } else {
//                    Log.d(TAG, response.errorBody()?.string().toString())
//                    Log.d(TAG,"following $list")
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
//                Log.d("failll", t.message.toString())
//                Log.d(TAG,"following $list")
            }
        })
        return list
    }
    private suspend fun getAIRecommendAlbums(): List<ReceivedAlbumModel> {
        var list = listOf<ReceivedAlbumModel>()
        albumService.getAIAlbums(accessToken!!, refreshToken!!).enqueue(object :
            Callback<AlbumResponse> {
            override fun onResponse(
                call: Call<AlbumResponse>,
                response: Response<AlbumResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.headers()["AccessToken"] != null) {
                        CoroutineScope(Dispatchers.IO).launch {
//                            Log.d(
//                                "new AccessToken",
//                                response.headers()["AccessToken"].toString()
//                            )
                            dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                        }
                    }
                    list = response.body()!!.albumListResult
//                    Log.d(TAG,"ai $list")
                } else {
//                    Log.d(TAG, response.errorBody()?.string().toString())
//                    Log.d(TAG,"ai $list")
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
//                Log.d("failll", t.message.toString())
//                Log.d(TAG,"ai $list")
            }
        })
        return list
    }
    private suspend fun getGenreRecommendAlbums(): List<ReceivedAlbumModel> {
        var list = listOf<ReceivedAlbumModel>()
        albumService.getGenreAlbums(accessToken!!, refreshToken!!).enqueue(object :
            Callback<AlbumResponse> {
            override fun onResponse(
                call: Call<AlbumResponse>,
                response: Response<AlbumResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.headers()["AccessToken"] != null) {
                        CoroutineScope(Dispatchers.IO).launch {
//                            Log.d(
//                                "new AccessToken",
//                                response.headers()["AccessToken"].toString()
//                            )
                            dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                        }
                    }
                    list = response.body()!!.albumListResult
//                    Log.d(TAG,"genre $list")
                } else {
//                    Log.d(TAG, response.errorBody()?.string().toString())
//                    Log.d(TAG,"genre $list")
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
//                Log.d("failll", t.message.toString())
//                Log.d(TAG,"genre $list")
            }
        })
        return list
    }

}