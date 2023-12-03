package com.ajou.diggingclub.intro.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentIntro4Binding
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.UserService
import com.ajou.diggingclub.utils.hideKeyboard
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntroFragment4 : Fragment() {

    private var _binding : FragmentIntro4Binding?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private var job: Job? = null
    private val userService = RetrofitInstance.getInstance().create(UserService::class.java)

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
        _binding = FragmentIntro4Binding.inflate(inflater,container,false)
        val view = binding.root
        binding.root.setOnClickListener{
            hideKeyboard(requireActivity())
        }
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
        }

        binding.removeBtn.setOnSingleClickListener {
            binding.nickname.setText("")
        }

        binding.backBtn.setOnSingleClickListener {
            findNavController().navigate(R.id.action_introFragment3_to_introFragment2)
        }

        binding.nickname.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setNickname(accessToken!!,refreshToken!!)
                return@setOnEditorActionListener true
            }else return@setOnEditorActionListener false

        }

        binding.nextBtn.setOnSingleClickListener {
            setNickname(accessToken!!,refreshToken!!)
        }

        binding.nickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                job?.cancel() // 이전 작업을 취소
                val nickname = str.toString().toLowerCase()
                Log.d("nickname",nickname)
                binding.nickname.removeTextChangedListener(this)
                binding.nickname.setText(nickname)
                binding.nickname.setSelection(nickname.length)
                binding.nickname.addTextChangedListener(this)
                if(nickname.isNotEmpty()){
                    job = CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)

                        val jsonObject = JsonObject().apply {
                            addProperty("nickname",nickname)
                        }
                        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())

                        accessToken = dataStore.getAccessToken().toString()

                        userService.checkNickname(accessToken,refreshToken,requestBody).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                binding.description.visibility = View.GONE
                                binding.defaultLine.visibility = View.GONE

                                CoroutineScope(Dispatchers.IO).launch {
                                    accessToken = dataStore.getAccessToken().toString()
                                }

                                if(response.isSuccessful){
                                    if(response.headers()["AccessToken"] != null) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                                        }
                                    }

                                    binding.complete.visibility = View.VISIBLE
                                    binding.possible.visibility = View.VISIBLE
                                    binding.warning.visibility = View.GONE
                                    binding.duplicate.visibility = View.GONE

                                    binding.nickname.setBackgroundResource(R.drawable.rectangle_3_ok)
                                    binding.nextBtn.isEnabled = true
                                    binding.nextBtn.setTextColor(resources.getColor(R.color.textColor))
                                    binding.nextBtn.setBackgroundResource(R.drawable.rectangle_2)
                                }else {
                                    val errorBody = JSONObject(response.errorBody()!!.string())
                                    binding.warning.visibility = View.VISIBLE
                                    binding.duplicate.text = errorBody.get("message").toString()
                                    binding.duplicate.visibility = View.VISIBLE
                                    binding.possible.visibility = View.GONE
                                    binding.complete.visibility = View.GONE

                                    binding.nickname.setBackgroundResource(R.drawable.rectangle_3_error)
                                    binding.nextBtn.isEnabled = false
                                    binding.nextBtn.setTextColor(resources.getColor(R.color.paleTextColor))
                                    binding.nextBtn.setBackgroundResource(R.drawable.rectangle_3)
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("fail",t.message.toString())
                            }
                        })
                    }
                }else{
                    binding.nickname.setBackgroundResource(R.drawable.rectangle_3_error)
                    binding.warning.visibility = View.VISIBLE
                    binding.complete.visibility = View.GONE
                    binding.duplicate.visibility = View.GONE
                    binding.possible.visibility = View.GONE
                    binding.nextBtn.isEnabled = false
                    binding.description.visibility = View.VISIBLE
                    binding.nextBtn.setBackgroundResource(R.drawable.rectangle_3)
                }

            }
        })
    }

    fun setNickname(accessToken : String, refreshToken : String){
        val jsonObject = JsonObject().apply {
            addProperty("nickname",binding.nickname.text.toString())
        }
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())

        userService.setNickname(accessToken,refreshToken,requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    CoroutineScope(Dispatchers.IO).launch {
                        UserDataStore().saveFirstFlag(true)
                    }
                    val action = IntroFragment4Directions.actionIntroFragment4ToIntroFragment5(binding.nickname.text.toString())
                    findNavController().navigate(action)
                }else{
                    Log.d("response",response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("fail",t.message.toString())
            }

        })
    }

}