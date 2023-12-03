package com.ajou.diggingclub.setting

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
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentWithdrawalBinding
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.UserService
import com.ajou.diggingclub.start.LandingActivity
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

class WithdrawalFragment : Fragment() {
    private var mContext : Context? = null
    private var _binding : FragmentWithdrawalBinding? = null
    private val binding get() = _binding!!
    private val userService = RetrofitInstance.getInstance().create(UserService::class.java)

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
        _binding = FragmentWithdrawalBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var reason : String ?= null
        var otherReason : String ?= null
        var accessToken : String? = null
        var refreshToken : String? = null
        val dataStore = UserDataStore()
        CoroutineScope(Dispatchers.IO).launch {
            val nickname = dataStore.getNickname()
            accessToken = dataStore.getAccessToken()
            refreshToken = dataStore.getRefreshToken()
            binding.withdrawalText.text = String.format(resources.getString(R.string.withdrawal),nickname,nickname)
        }

        binding.agreeCheck.setOnClickListener {
            binding.withdrawalBtn.isEnabled = binding.agreeCheck.isChecked
        }
        binding.option6.setOnClickListener {
            if(binding.option6.isChecked){
                binding.opinion.visibility = View.VISIBLE
            }else{
                binding.opinion.visibility = View.GONE
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.useBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.withdrawalBtn.setOnClickListener {
            Log.d("reason",reason.toString())
            Log.d("otherReason",otherReason.toString())
            val jsonObject = JsonObject().apply {
                addProperty("reason", reason)
                addProperty("otherReason", otherReason)
            }
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())

            userService.withdrawal(accessToken!!, refreshToken!!, requestBody).enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        Log.d("response",response.body().toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            UserDataStore().deleteAll() // 유저 데이터 삭제
                        }
                        val intent = Intent(mContext!!,LandingActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }else{
                        Log.d("error withdrawl",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("fail withdrawal",t.message.toString())
                }

            })
        }
        binding.radioGroup.setOnCheckedChangeListener{ group, checkId ->
            when(checkId){
                R.id.option1 -> reason = "REASON_1"
                R.id.option2 -> reason = "REASON_2"
                R.id.option3 -> reason = "REASON_3"
                R.id.option4 -> reason = "REASON_4"
                R.id.option5 -> reason = "REASON_5"
                R.id.option6 -> reason = "REASON_6"
            }
            if(reason != "REASON_6"){
                binding.opinion.text = null
                binding.opinion.visibility = View.GONE
            }else binding.opinion.visibility = View.VISIBLE
        }

        binding.opinion.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(str: Editable?) {
                otherReason = str.toString()
            }
        })
    }

}