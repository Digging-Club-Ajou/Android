package com.ajou.diggingclub.melody.album

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentMakeAlbum3Binding
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.AlbumApi
import com.ajou.diggingclub.start.LandingActivity
import com.ajou.diggingclub.utils.getMultipartFile
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakeAlbumFragment3 : Fragment() {

    private var _binding: FragmentMakeAlbum3Binding? = null
    private val binding get() = _binding!!
    private var job: Job? = null
    private val client = RetrofitInstance.getInstance().create(AlbumApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMakeAlbum3Binding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: MakeAlbumFragment3Args by navArgs()

        val dataStore = UserDataStore()

        var accessToken : String? = null
        var refreshToken : String? = null

        CoroutineScope(Dispatchers.IO).launch {
            var nickname = dataStore.getNickname().toString()
            binding.nickname.text = nickname
            accessToken = dataStore.getAccessToken()
            refreshToken = dataStore.getRefreshToken()
            if(accessToken == null || refreshToken == null){
                val intent = Intent(requireActivity(), LandingActivity::class.java)
                startActivity(intent)
            }
        }

        val parsedUri = Uri.parse(args.uri)
        val multiOptions = MultiTransformation(
            CenterCrop(),
            RoundedCorners(10)
        ) // glide 옵션

        Glide.with(requireActivity())
            .load(parsedUri)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(multiOptions))
            .into(binding.image)

        val img = getMultipartFile(parsedUri,requireActivity(),"albumImage")

        binding.done.setOnClickListener {
            val jsonObject = JsonObject().apply {
                addProperty("albumName",binding.title.text.toString())
            }
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())

            client.createAlbum(accessToken!!,refreshToken!!,requestBody,img).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        findNavController().navigate(R.id.action_makeAlbumFragment3_to_findMusicFragment)
                    }else{
                        Log.d("response not success",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("fail",t.message.toString())
                }

            })
        }


        binding.title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                job?.cancel() // 이전 작업을 취소
                if(str.toString().isNotEmpty()){
                    job = CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)

                        val jsonObject = JsonObject().apply {
                            addProperty("albumName",str.toString())
                        }
                        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
                        accessToken = dataStore.getAccessToken().toString()
                        refreshToken = dataStore.getRefreshToken().toString()

                        client.checkAlbumsName(accessToken!!,refreshToken!!,requestBody).enqueue(object :
                            Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                if(response.isSuccessful){
                                    if(response.headers()["AccessToken"] != null) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                        }
                                    }
                                    binding.description.text = "한글, 영문, 숫자를 포함한 15자를 지원합니다."
                                    binding.description.setTextColor(Color.parseColor("#767676"))
                                    binding.done.setTextColor(resources.getColor(R.color.textColor))
                                    binding.done.isEnabled = true
                                }else{
                                    val errorBody = JSONObject(response.errorBody()!!.string())
                                    binding.description.text = errorBody.get("message").toString()
                                    binding.description.setTextColor(resources.getColor(R.color.errorColor))
                                    binding.done.setTextColor(resources.getColor(R.color.paleTextColor))
                                    binding.done.isEnabled = false
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("fail",t.message.toString())
                            }

                        })
                    }
                }
            }

        })
    }
}