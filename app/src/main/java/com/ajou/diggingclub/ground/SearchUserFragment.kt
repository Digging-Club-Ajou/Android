package com.ajou.diggingclub.ground

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentSearchUserBinding
import com.ajou.diggingclub.ground.adapter.SearchUserRVAdapter
import com.ajou.diggingclub.ground.models.MemberSearchModel
import com.ajou.diggingclub.melody.card.adapter.MusicListRVAdapter
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.MemberApi
import com.ajou.diggingclub.network.api.TmpApi
import com.ajou.diggingclub.network.models.MemberSearchResponse
import com.ajou.diggingclub.network.models.SpotifyResponse
import com.ajou.diggingclub.start.LandingActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchUserFragment : Fragment() {
    private var _binding : FragmentSearchUserBinding ?= null
    private val binding get() = _binding!!
    private var job: Job? = null
    private val client = RetrofitInstance.getInstance().create(TmpApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(requireContext(), LandingActivity::class.java)
                startActivity(intent)
            }
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
                        binding.removeBtn.visibility = View.VISIBLE
                        job = CoroutineScope(Dispatchers.IO).launch {
                            delay(1000)
                            accessToken = dataStore.getAccessToken().toString()
                            refreshToken = dataStore.getRefreshToken().toString()
                            Log.d("str",str.toString())
                            Log.d("accessToken",accessToken.toString())
                            Log.d("refreshToken",refreshToken.toString())
                            val jsonObject = JsonObject().apply {
                                addProperty("keyword", str.toString())
                            }
                            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
                            client.searchMember(accessToken!!,refreshToken!!,requestBody).enqueue(object :
                                Callback<MemberSearchResponse> {
                                override fun onResponse(
                                    call: Call<MemberSearchResponse>,
                                    response: Response<MemberSearchResponse>
                                ) {
                                    if(response.isSuccessful){
                                        if(response.headers()["AccessToken"] != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                            }
                                        }
                                        val list : List<MemberSearchModel> = response.body()!!.memberSearchListResult
                                        Log.d("success",list.toString())
                                        val userListRVAdapter = SearchUserRVAdapter(requireContext(),list)
                                        binding.listRV.adapter = userListRVAdapter
                                        binding.listRV.layoutManager = LinearLayoutManager(requireContext())
                                        // 이후에 api 추가해서 수정하기
                                    }else {
                                        Log.d("response not successful",response.errorBody()?.string().toString())
                                    }
                                }

                                override fun onFailure(
                                    call: Call<MemberSearchResponse>,
                                    t: Throwable
                                ) {
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
}