package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentSearchUserBinding
import com.ajou.diggingclub.ground.adapter.SearchUserRVAdapter
import com.ajou.diggingclub.ground.models.MemberSearchModel
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumService
import com.ajou.diggingclub.network.api.SearchService
import com.ajou.diggingclub.network.models.MemberSearchResponse
import com.ajou.diggingclub.profile.ProfileActivity
import com.ajou.diggingclub.start.LandingActivity
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchUserFragment : Fragment() {
    private var _binding : FragmentSearchUserBinding ?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null

    private var job: Job? = null
    private val searchService = RetrofitInstance.getInstance().create(SearchService::class.java)
    private val albumClient = RetrofitInstance.getInstance().create(AlbumService::class.java)

    private var list : ArrayList<ReceivedAlbumModel> = arrayListOf()
    var userId : String? = null
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
        _binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null
        val link = AdapterToFragment()

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            userId = dataStore.getMemberId().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                job?.cancel()
                if (str != null) {
                    if(str.isNotEmpty()) {
                        list.clear()
                        binding.removeBtn.visibility = View.VISIBLE
                        job = CoroutineScope(Dispatchers.IO).launch {
                            delay(1000)
                            accessToken = dataStore.getAccessToken().toString()
                            refreshToken = dataStore.getRefreshToken().toString()
                            searchService.searchMember(accessToken!!,refreshToken!!,str.toString()).enqueue(object :
                                Callback<MemberSearchResponse> {
                                override fun onResponse(
                                    call: Call<MemberSearchResponse>,
                                    response: Response<MemberSearchResponse>
                                ) {
                                    if(response.isSuccessful) {
                                        if (response.headers()["AccessToken"] != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                            }
                                        }
                                        val memberSearchListResult: List<MemberSearchModel> =
                                            response.body()!!.memberSearchListResult
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val userList = memberSearchListResult.map { memberSearchModel ->
                                                    async {
                                                        val albumResponse = albumClient.getAlbum(
                                                            accessToken!!,
                                                            refreshToken!!,
                                                            memberSearchModel.albumId
                                                        ).execute()
                                                        albumResponse.body()
                                                    }
                                                }.awaitAll().filterNotNull()
                                            Log.d("data userlist",userList.toString())
                                            list.addAll(userList)
                                            withContext(Dispatchers.Main) {
                                                val userListRVAdapter = SearchUserRVAdapter(mContext!!, userList,link)
                                                binding.listRV.adapter = userListRVAdapter
                                                binding.listRV.layoutManager = LinearLayoutManager(mContext)
                                            }
                                        }
                                    }
                                    else {
                                        Log.d("response not successful",response.errorBody()?.string().toString())
                                    }
                                }
                                override fun onFailure(call: Call<MemberSearchResponse>, t: Throwable) {
                                    Log.d("fail",t.message.toString())
                                }
                            })
                        }
                    }else{
                        binding.removeBtn.visibility = View.GONE
                    }
                }
            }
        })
    }
    inner class AdapterToFragment {
        fun getSelectedItem(position : Int) {
            val data = list[position]
            val albumInfo = ReceivedAlbumModel(data.memberId,data.albumId,data.nickname,data.albumName,data.imageUrl,data.artistNames)
            if(userId.toString() == data.memberId.toString()){
                val intent = Intent(mContext, ProfileActivity::class.java)
                intent.putExtra("albumInfo",data)
                startActivity(intent)
            }else{
                val action =
                    SearchUserFragmentDirections.actionSearchUserFragmentToUserArchiveFragment(albumInfo,data.albumId.toString(),data.memberId.toString())
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        list.clear()
    }
}