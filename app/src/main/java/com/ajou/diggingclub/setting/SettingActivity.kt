package com.ajou.diggingclub.setting

import android.content.Intent
import android.graphics.Outline
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.view.ViewCompat
import com.ajou.diggingclub.R
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.ActivitySettingBinding
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.melody.MelodyActivity
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.UserService
import com.ajou.diggingclub.profile.ProfileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SettingActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySettingBinding
    private val userService = RetrofitInstance.getInstance().create(UserService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var accessToken : String? = null
        var refreshToken : String? = null
        var jsonObject = JSONObject()

        CoroutineScope(Dispatchers.IO).launch {
            val nickname = UserDataStore().getNickname()
            accessToken = UserDataStore().getAccessToken()
            refreshToken = UserDataStore().getRefreshToken()
            binding.nickname.text = nickname
            val response = userService.getUserInfo(accessToken!!,refreshToken!!)
            if(response.isSuccessful){
                jsonObject = JSONObject(response.body()?.string())
                var nickname = "-"
                var gender = "-"
                var phoneNumber = "-"
                var email = "-"
                if(jsonObject.getString("nickname")!="null") nickname = jsonObject.getString("nickname")
                if(jsonObject.getString("gender")!="null") gender = jsonObject.getString("gender")
                if(jsonObject.getString("phoneNumber")!="null") phoneNumber = jsonObject.getString("phoneNumber")
                if(jsonObject.getString("email")!="null") email = jsonObject.getString("email")

                withContext(Dispatchers.Main){
                    binding.nickname.text = nickname
                    binding.gender.text = gender
                    binding.phoneNumber.text = phoneNumber
                    binding.email.text = email
                }
            }else{
                Log.d("error",response.errorBody()?.string().toString())
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.tos.setOnClickListener {
            val intent = Intent(this, ToSActivity::class.java)
            startActivity(intent)
        }
        binding.privacyPolicy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
        binding.guide.setOnClickListener {
            val intent = Intent(this, GuideLineActivity::class.java)
            startActivity(intent)
        }
        binding.logout.setOnClickListener {
            val intent = Intent(this, SignOutActivity::class.java)
            startActivity(intent)
        }
        binding.switchBtn.setOnClickListener {
            binding.switchBtn.isSelected = !binding.switchBtn.isSelected
        }
        binding.tabGround.setOnClickListener {
            finish()
            val intent = Intent(this, GroundActivity::class.java)
            startActivity(intent)
        }
        binding.tabMelody.setOnClickListener {
            finish()
            val intent = Intent(this, MelodyActivity::class.java)
            startActivity(intent)
        }
        binding.tabProfile.setOnClickListener {
            finish()
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }
}