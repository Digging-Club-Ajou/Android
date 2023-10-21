package com.ajou.diggingclub.melody.card

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
import com.ajou.diggingclub.databinding.FragmentSearchMusicBinding
import com.ajou.diggingclub.melody.card.adapter.MusicListRVAdapter
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.ajou.diggingclub.network.models.SpotifyResponse
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.MusicApi
import com.ajou.diggingclub.start.LandingActivity
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMusicFragment : Fragment() {

    private var _binding: FragmentSearchMusicBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null
    private val client = RetrofitInstance.getInstance().create(MusicApi::class.java)

    inner class AdapterToFragment {
        fun getSelectedItem(data : MusicSpotifyModel) {
            val action = SearchMusicFragmentDirections.actionFindMusicFragmentToMakeCardFragment1(data)
            Log.d("data",data.toString())
            findNavController().navigate(action)
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
        _binding = FragmentSearchMusicBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
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
        binding.removeBtn.setOnClickListener {
            binding.editText.setText("")
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
                        binding.textView12.visibility = View.GONE
                        binding.removeBtn.visibility = View.VISIBLE
                        job = CoroutineScope(Dispatchers.IO).launch {
                            delay(1000)
                            accessToken = dataStore.getAccessToken().toString()
                            refreshToken = dataStore.getRefreshToken().toString()
                            
                            client.searchMusic(accessToken!!,refreshToken!!,str.toString()).enqueue(object : Callback<SpotifyResponse> {
                                override fun onResponse(
                                    call: Call<SpotifyResponse>,
                                    response: Response<SpotifyResponse>
                                ) {
                                    if(response.isSuccessful){
                                        if(response.headers()["AccessToken"] != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                            }
                                        }
                                        val list : List<MusicSpotifyModel> = response.body()!!.spotifyListResult
                                        Log.d("success",list.toString())
                                        val link = AdapterToFragment()
                                        val musicListRVAdapter = MusicListRVAdapter(requireContext(),list,link)
                                        binding.listRV.adapter = musicListRVAdapter
                                        binding.listRV.layoutManager = LinearLayoutManager(requireContext())
                                    }else {
                                        Log.d("response not successful",response.errorBody()?.string().toString())
                                    }
                                }

                                override fun onFailure(call: Call<SpotifyResponse>, t: Throwable) {
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