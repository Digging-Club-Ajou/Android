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
import androidx.recyclerview.widget.GridLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentAlbumListBinding
import com.ajou.diggingclub.ground.adapter.FollowingAlbumListRVAdapter
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.models.AlbumResponse
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.AdapterToFragment
import com.ajou.diggingclub.utils.fromDpToPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumListFragment : Fragment(), AdapterToFragment {
    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private val albumService = RetrofitInstance.getInstance().create(AlbumService::class.java)

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

        val dataStore = UserDataStore()
        var accessToken: String? = null
        var refreshToken: String? = null
        var nickname: String? = null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            nickname = dataStore.getNickname().toString()
            val tmp = "AI가 " + nickname + "님에게 추천하는 앨범이에요!"
            binding.name.text = tmp
            if (accessToken == null || refreshToken == null) {
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }

            binding.backBtn.setOnClickListener {
                findNavController().navigate(R.id.action_albumListFragment_to_groundFragment)
            }

            withContext(Dispatchers.Main) {
                // TODO type을 받아서 type에 따라 api 호출하기 following, AI 등등..
                albumService.getFollowingAlbums(accessToken!!, refreshToken!!).enqueue(object :
                    Callback<AlbumResponse> {
                    override fun onResponse(
                        call: Call<AlbumResponse>,
                        response: Response<AlbumResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.headers()["AccessToken"] != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    Log.d(
                                        "new AccessToken",
                                        response.headers()["AccessToken"].toString()
                                    )
                                    dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                }
                            }
                            val list: List<ReceivedAlbumModel> = response.body()!!.albumListResult
                            Log.d("success", list.toString())
                            val albumListRVAdapter =
                                FollowingAlbumListRVAdapter(mContext!!, list, "following", this@AlbumListFragment)
                            binding.albumRV.adapter = albumListRVAdapter
                            val gridLayoutManager = GridLayoutManager(mContext, 2)
                            binding.albumRV.addItemDecoration(
                                FollowingAlbumListRVAdapter.GridSpacingItemDecoration(
                                    2,
                                    8f.fromDpToPx()
                                )
                            )
                            binding.albumRV.layoutManager = gridLayoutManager

                        } else {
                            Log.d(
                                "response not successful",
                                response.errorBody()?.string().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                        Log.d("failll", t.message.toString())
                    }
                })
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_albumListFragment_to_groundFragment)
        }
    }
    // TODO 어댑터들 연결해야함 GroundFragment처럼

    override fun getSelectedItem(item: ReceivedAlbumModel) {
        // TODO
    }

    override fun getSelectedId(memberId: String, albumId: String, name : String, type: String) {
        // TODO
    }

    override fun postFavoriteId(melodyCardId: String, isLike: Boolean) {
        // TODO
    }

    override fun addRecordCount(position: Int) {
        // TODO
    }
}