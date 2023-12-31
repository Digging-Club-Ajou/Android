package com.ajou.diggingclub.intro.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentIntro2Binding
import com.ajou.diggingclub.intro.*
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.ArtistService
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.getJsonDataFromAssets
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntroFragment2 : Fragment() {

    private var _binding : FragmentIntro2Binding?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    var artistInfoList : List<ArtistInfoModel> = arrayListOf()
    var categoryList : ArrayList<String> = arrayListOf()
    var likedList : ArrayList<String> = arrayListOf()

    private val artistService = RetrofitInstance.getInstance().create(ArtistService::class.java)
    private val viewModel : IntroViewModel by viewModels()
    inner class AdapterToFragment {
        fun getSelectedItem(data : IntroSelectModel, position : Int) {
            if(data.selected){
                viewModel.addArtistArr(position)
                likedList.add(data.text)
            }
            else{
                viewModel.removeArtistArr(position)
                likedList.remove(data.text)
            }
            Log.d("likedList",likedList.toString())
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsonString = getJsonDataFromAssets(requireActivity())
        val gson = Gson()
        val listArtistInfo = object : TypeToken<List<ArtistInfoModel>>() {}.type
        artistInfoList = gson.fromJson(jsonString, listArtistInfo)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntro2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val link = AdapterToFragment()
        val dataStore = UserDataStore()
        var accessToken : String? = null
        var refreshToken : String? = null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }

        artistInfoList.map {
            if(!categoryList.contains(it.category)) categoryList.add(it.category)
        }
        val adapter = IntroArtistRVAdapter(mContext!!,categoryList,artistInfoList,link)
        binding.view.adapter = adapter
        binding.view.layoutManager = LinearLayoutManager(mContext)

        viewModel.artistArr.observe(requireActivity(), Observer {
            binding.nextBtn.text = "다음(${it.size}/5)"
            if(it.size==5){
                adapter.setBlock(false)
                binding.nextBtn.setBackgroundResource(R.drawable.rectangle_2)
                binding.nextBtn.setTextColor(resources.getColor(R.color.textColor))
                binding.nextBtn.isEnabled = true
            }else{
                adapter.setBlock(true)
                binding.nextBtn.setBackgroundResource(R.drawable.rectangle_3)
                binding.nextBtn.setTextColor(resources.getColor(R.color.paleTextColor))
                binding.nextBtn.isEnabled = false
            }
        })
        binding.nextBtn.setOnSingleClickListener {
            val jsonObject = JSONObject().apply {
                put("artistNames", JSONArray(likedList))
            }
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
            artistService.postArtists(accessToken!!,refreshToken!!,requestBody).enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        accessToken = dataStore.getAccessToken().toString()
                    }

                    if(response.isSuccessful){
                        if(response.headers()["AccessToken"] != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                            }
                        }
                        findNavController().navigate(R.id.action_introFragment2_to_introFragment3)
                    }else{
                        Log.d("response not succeesss",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("faillll",t.message.toString())
                }

            })
        }
        if(viewModel.artistArr.value?.isNotEmpty() == true){
            for(i in 0 until viewModel.artistArr.value!!.size) {
                Log.d("viewModel", viewModel.artistArr.value!![i].toString())
                // TODO 어떻게 값에 따라 클릭된 상태로 보여줄지 생각해보기
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setEmptyArtistArr()
    }
}