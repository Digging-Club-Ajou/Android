package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentFollowingListBinding
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.FollowingApi
import com.ajou.diggingclub.network.models.FollowingResponseBody
import com.ajou.diggingclub.start.LandingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call

class FollowingListFragment : Fragment() {
    private var _binding : FragmentFollowingListBinding?= null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val client = RetrofitInstance.getInstance().create(FollowingApi::class.java)

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
        _binding = FragmentFollowingListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        var accessToken: String? = null
        var refreshToken: String? = null
        var memberId : Int? = null

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            memberId = dataStore.getMemberId()
            if (accessToken == null || refreshToken == null) {
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
        }
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("팔로잉"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("팔로워"))

        client.getFollowings(accessToken!!, refreshToken!!, memberId!!)
            .enqueue(object : retrofit2.Callback<FollowingResponseBody> {
                override fun onResponse(
                    call: Call<FollowingResponseBody>,
                    response: retrofit2.Response<FollowingResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val followings = response.body()?.followings
                        val followers = response.body()?.followers
                        Log.d("followings",followings.toString())
                        Log.d("followers",followers.toString())
                    }else{
                        Log.d("error",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<FollowingResponseBody>, t: Throwable) {
                    Log.d("fail",t.message.toString())
                }
            })
    }
}