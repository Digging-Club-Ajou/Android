package com.ajou.diggingclub

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ajou.diggingclub.databinding.ActivitySignUpBinding
import com.ajou.diggingclub.network.api.Api
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.models.SignUpRequestBody
import com.ajou.diggingclub.network.models.SignUpResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : AppCompatActivity() {
    private var _binding : ActivitySignUpBinding ?= null
    private val binding get() = _binding!!
    private val client = RetrofitInstance.getInstance().create(Api::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val code : String = intent.getStringExtra("authCode")!!
        Log.d("code",code)
        var gender : String = "MALE"
        var isFormatting : Boolean = false
        var phoneNumberFormat : String = ""
//        binding.nickname.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.d("before",p0.toString())
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.d("onTextChanged",p0.toString())
//            }
//
//            override fun afterTextChanged(str: Editable?) {
//                if(str!=null){
//            }
//
//        })
        binding.femaleBtn.setOnClickListener {
            gender = "FEMALE"
        }
        binding.maleBtn.setOnClickListener {
            gender = "MALE"
        }
        binding.phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("before",p0.toString())
            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!isFormatting) {
                    isFormatting = true
                    val digits: String = str.toString().replace("[^\\d]","")
                    Log.d("digits",digits)
                    phoneNumberFormat = formatPhoneNumber(digits)
                    binding.phoneNumber.setText(phoneNumberFormat)
                    binding.phoneNumber.setSelection(phoneNumberFormat.length)
                    isFormatting = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("after",p0.toString())
            }

        })
        binding.nextBtn.setOnClickListener {
            var check = binding.pwd.text.toString()
            if(binding.pwd.text.toString() != binding.pwdCheck.text.toString()) check = "일치하지 않는 비밀번호"
            val userData = SignUpRequestBody(
                binding.username.text.toString(),
                binding.id.text.toString(),
                binding.pwd.text.toString(),
                check,
                binding.phoneNumber.text.toString(),
                binding.email.text.toString(),
                gender
            )
            client.signUpUser(userData).enqueue(object : Callback<SignUpResponseBody> {
                override fun onResponse(
                    call: Call<SignUpResponseBody>,
                    response: Response<SignUpResponseBody>
                ) {
                    if(response.isSuccessful){
                        Log.d("회원가입 성공",response.body().toString())
                    }else{
                        Log.d("userData",userData.toString())
                        Log.d("회원가입 not successful",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<SignUpResponseBody>, t: Throwable) {
                    Log.d("회원가입 실패",t.message.toString())
                }
            })
            // 리스트로 값 넣어서 넘기기
        }
    }
    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.length < 4) {
            phoneNumber
        } else if (phoneNumber.length < 7) {
            phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3)
        } else {
            phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(
                3,
                7
            ) + "-" + phoneNumber.substring(7)
        }
    }
}