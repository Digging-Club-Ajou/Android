package com.ajou.diggingclub.melody.card

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentSearchMusicBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.SearchDataViewModel
import com.ajou.diggingclub.melody.card.adapter.MusicListRVAdapter
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.ajou.diggingclub.network.models.SpotifyResponse
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.MusicService
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.hideKeyboard
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMusicFragment : Fragment() {

    private var mContext: Context? = null
    private var _binding: FragmentSearchMusicBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null
    private val musicService = RetrofitInstance.getInstance().create(MusicService::class.java)
    private lateinit var viewModel : SearchDataViewModel
    private lateinit var adapter : MusicListRVAdapter

    val list : ArrayList<MusicSpotifyModel> = arrayListOf()
    var isEnd = false
    val dataStore = UserDataStore()
    var page = 1
    var isLoading = false

    inner class AdapterToFragment {
        fun getSelectedItem(data : MusicSpotifyModel) {
            val action = SearchMusicFragmentDirections.actionFindMusicFragmentToMakeCardFragment1(data)
            Log.d("data",data.toString())
            findNavController().navigate(action)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
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
        binding.root.setOnClickListener{
            Log.d("clicked!","clicked!")
            hideKeyboard(requireActivity())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var accessToken : String? = null
        var refreshToken : String? = null
        var keyword = ""

        viewModel = ViewModelProvider(requireActivity())[SearchDataViewModel::class.java]
        viewModel.setEmptyList()

        val link = AdapterToFragment()
        adapter = MusicListRVAdapter(mContext!!, emptyList(),link)
        binding.listRV.adapter = adapter
        binding.listRV.layoutManager = LinearLayoutManager(mContext)

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }
        binding.removeBtn.setOnClickListener {
            binding.editText.setText("")
            viewModel.setEmptyList()
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(mContext, GroundActivity::class.java)
            startActivity(intent)
        }

        binding.listRV.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!binding.listRV.canScrollVertically(1)&&!isEnd&&!isLoading){
                        getData(accessToken!!,refreshToken!!,keyword)
                    isLoading = true
                }
            }
        })

        binding.editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(str: Editable?) {
                job?.cancel()
                if (str != null) {
                    if(str.isNotEmpty()) {
                        viewModel.setEmptyList()
                        page = 1
                        binding.textView12.visibility = View.GONE
                        binding.removeBtn.visibility = View.VISIBLE
                        job = CoroutineScope(Dispatchers.IO).launch {
                            delay(500)
                            accessToken = dataStore.getAccessToken().toString()
                            refreshToken = dataStore.getRefreshToken().toString()
                            keyword = str.toString()
                            getData(accessToken!!,refreshToken!!,keyword)
                        }
                    }else{
                        binding.removeBtn.visibility = View.GONE
                    }
                }
            }
        })
    }
    private fun getData(accessToken : String, refreshToken : String, keyword : String){
        musicService.searchMusic(accessToken,refreshToken,keyword, page.toString()).enqueue(object : Callback<SpotifyResponse>{
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
                    val data : List<MusicSpotifyModel> = response.body()!!.spotifyListResult
                    Log.d("success",data.toString())
                    if(data.isEmpty()){
                        isEnd = true
                    }else{
                        viewModel.addSearchList(data)
                        adapter.updateList(viewModel.list.value!!)
                        page++
                        isLoading = false
                    }
                }else{
                    Log.d("response not successful",response.errorBody()?.string().toString())
                    isEnd = true
                }
            }

            override fun onFailure(call: Call<SpotifyResponse>, t: Throwable) {
                Log.d("fail",t.message.toString())
                isEnd = true
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::viewModel.isInitialized) viewModel.setEmptyList()
    }
}