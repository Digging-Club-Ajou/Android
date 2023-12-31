package com.ajou.diggingclub.intro.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentIntro1Binding
import com.ajou.diggingclub.intro.IntroGenreRVAdapter
import com.ajou.diggingclub.intro.IntroSelectModel
import com.ajou.diggingclub.intro.IntroViewModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.GenreService
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.fromDpToPx
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntroFragment1 : Fragment() {
    private var _binding : FragmentIntro1Binding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val genreService = RetrofitInstance.getInstance().create(GenreService::class.java)
    private val viewModel : IntroViewModel by viewModels()

    inner class AdapterToFragment {
        fun getSelectedItem(data : IntroSelectModel, position : Int) {
            if(data.selected)
                viewModel.addGenreArr(position)
            else
                viewModel.removeGenreArr(position)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntro1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataStore = UserDataStore()
        var accessToken: String? = null
        var refreshToken: String? = null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if (accessToken == null || refreshToken == null) {
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }

        val itemList: ArrayList<IntroSelectModel> = arrayListOf()
        itemList.add(IntroSelectModel("인디", R.drawable.genreindi, null, false, "indie"))
        itemList.add(IntroSelectModel("발라드", R.drawable.genreballad, null, false, "ballade"))
        itemList.add(IntroSelectModel("댄스", R.drawable.genredance, null, false, "dance"))
        itemList.add(IntroSelectModel("록/메탈", R.drawable.genremetal, null, false, "rockMetal"))
        itemList.add(IntroSelectModel("R&B/Soul", R.drawable.genresoul, null, false, "rbAndSoul"))
        itemList.add(IntroSelectModel("랩/힙합", R.drawable.genrehiphop, null, false, "rapHiphop"))
        itemList.add(IntroSelectModel("포크/블루스", R.drawable.genrefolk, null, false, "folkBlues"))
        itemList.add(IntroSelectModel("POP", R.drawable.genrepop, null, false, "pop"))
        itemList.add(
            IntroSelectModel(
                "OST/뮤지컬",
                R.drawable.genremusical,
                null,
                false,
                "ostAndMusical"
            )
        )

        val link = AdapterToFragment()

        val adapter = IntroGenreRVAdapter(mContext!!, itemList, link)
        binding.view.adapter = adapter
        val gridLayoutManager = GridLayoutManager(mContext, 3)
        binding.view.addItemDecoration(
            IntroGenreRVAdapter.GridSpacingItemDecoration(
                3,
                16f.fromDpToPx()
            )
        )

        binding.view.layoutManager = gridLayoutManager
        binding.nextBtn.setOnSingleClickListener {
            val genreObject = JsonObject()
            for (i in 0 until itemList.size) {
                if (viewModel.genreArr.value!!.contains(i))
                    genreObject.addProperty(itemList[i].genre, true)
                else
                    genreObject.addProperty(itemList[i].genre, false)
            }
            val requestBody =
                RequestBody.create("application/json".toMediaTypeOrNull(), genreObject.toString())
            genreService.postGenre(accessToken!!, refreshToken!!, requestBody).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        if (response.headers()["AccessToken"] != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                            }
                        }
                        findNavController().navigate(R.id.action_introFragment1_to_introFragment2)
                    } else {
//                        Log.d("response not succeesss", response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Log.d("faill", t.message.toString())
                }
            })
        }

        viewModel.genreArr.observe(requireActivity(), Observer {
            binding.nextBtn.text = "다음(${it.size}/5)"
            if (it.size == 5) {
                adapter.blockAdd(false)
                binding.nextBtn.setBackgroundResource(R.drawable.rectangle_2)
                binding.nextBtn.setTextColor(resources.getColor(R.color.textColor))
                binding.nextBtn.isEnabled = true
            } else {
                adapter.blockAdd(true)
                binding.nextBtn.setBackgroundResource(R.drawable.rectangle_3)
                binding.nextBtn.setTextColor(resources.getColor(R.color.paleTextColor))
                binding.nextBtn.isEnabled = false
            }
        })

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // 뒤로가기 버튼 동작을 막음
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        if(viewModel.genreArr.value?.isNotEmpty() == true){
            for(i in 0 until viewModel.genreArr.value!!.size) itemList[viewModel.genreArr.value!![i]].selected = true
        }
    }

}