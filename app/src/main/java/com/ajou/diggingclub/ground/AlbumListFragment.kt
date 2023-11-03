package com.ajou.diggingclub.ground

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentAlbumListBinding
import com.ajou.diggingclub.ground.adapter.FollowingAlbumListRVAdapter
import com.ajou.diggingclub.ground.adapter.HashtagRVAdapter
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.intro.IntroGenreRVAdapter
import com.ajou.diggingclub.melody.card.SearchLocationFragmentDirections
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumApi
import com.ajou.diggingclub.network.models.FollwingAlbumResponse
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.fromDpToPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumListFragment : Fragment() {
    private var _binding : FragmentAlbumListBinding? = null
    private val binding get() = _binding!!
    private val client = RetrofitInstance.getInstance().create(AlbumApi::class.java)

    inner class AdapterToFragment {
        fun getSelectedItem(data : String) {
//            val action =
//                SearchLocationFragmentDirections.actionSearchLocationFragmentToShareCardFragment(
//                    args.uri,
//                    args.color,
//                    data,
//                    args.music
//                )
//            findNavController().navigate(action)
            // 선택한 앨범 졍보 전달(albumName, albumId, nickname)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        var accessToken : String? = null
        var refreshToken : String? = null
        var nickname : String ?= null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            nickname = dataStore.getNickname().toString()
            val tmp = "AI가 "+nickname+"님에게 추천하는 앨범이에요!"
            binding.name.text = tmp
            if(accessToken == null || refreshToken == null){
                val intent = Intent(requireContext(), LandingActivity::class.java)
                startActivity(intent)
            }
        }

        val hashtagList : ArrayList<String> = arrayListOf("댄스","기분전환","여자","댄스","기분전환","여자","댄스","기분전환","여자")
        val hashtagAdapter = HashtagRVAdapter(requireContext(),hashtagList)
        binding.hashtagRV.adapter = hashtagAdapter
        binding.hashtagRV.addItemDecoration(HashtagRVAdapter.GridSpacingItemDecoration(2,8f.fromDpToPx()))

        client.getFollowingAlbums(accessToken!!,refreshToken!!).enqueue(object :
            Callback<FollwingAlbumResponse> {
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
                    val albumListRVAdapter = FollowingAlbumListRVAdapter(requireContext(),list,"following")
                    binding.albumRV.adapter = albumListRVAdapter
                    val gridLayoutManager = GridLayoutManager(requireContext(),2)
                    binding.albumRV.addItemDecoration(FollowingAlbumListRVAdapter.GridSpacingItemDecoration(2,8f.fromDpToPx()))
                    binding.albumRV.layoutManager = gridLayoutManager

                }else{
                    Log.d("response not successful",response.errorBody()?.string().toString())
                }
            }

            override fun onFailure(call: Call<FollwingAlbumResponse>, t: Throwable) {
                Log.d("failll",t.message.toString())
            }
        })

    }
}