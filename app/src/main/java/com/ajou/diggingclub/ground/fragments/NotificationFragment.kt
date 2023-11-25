package com.ajou.diggingclub.ground.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.databinding.FragmentNotificationBinding
import com.ajou.diggingclub.ground.adapter.NotificationRVAdapter
import com.ajou.diggingclub.ground.models.NotificationsModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.NotificationService
import com.ajou.diggingclub.network.models.NotificationsResponse
import com.ajou.diggingclub.start.LandingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class NotificationFragment : Fragment() {
    private var _binding : FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val notificationService = RetrofitInstance.getInstance().create(NotificationService::class.java)

    var accessToken : String? = null
    var refreshToken : String? = null

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
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = UserDataStore()
        val link = DeleteNotification()
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            refreshToken = dataStore.getRefreshToken().toString()
            if (accessToken == null || refreshToken == null){
                val intent = Intent(mContext, LandingActivity::class.java)
                startActivity(intent)
            }
            notificationService.getNotification(accessToken!!,refreshToken!!).enqueue(object : retrofit2.Callback<NotificationsResponse>{
                override fun onResponse(
                    call: Call<NotificationsResponse>,
                    response: Response<NotificationsResponse>
                ) {
                    if(response.isSuccessful){
                        if(response.headers()["AccessToken"] != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStore.saveAccessToken(response.headers()["AccessToken"].toString())
                            }
                        }else{
                            val list : List<NotificationsModel> = response.body()!!.notificationsList
                            Log.d("list",list.toString())
                            val notiListRVAdapter = NotificationRVAdapter(mContext!!,list,link)
                            binding.notiRV.adapter = notiListRVAdapter
                            binding.notiRV.layoutManager = LinearLayoutManager(mContext)
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                    Log.d("fail",t.message.toString())
                }
            })
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    inner class DeleteNotification{
        // TODO 삭제 버튼 클릭 시 바로 사라지도록 해야할 듯
        fun deleteNotification(notificationId : String){
            notificationService.deleteNotification(accessToken!!,refreshToken!!,notificationId).enqueue(object : retrofit2.Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful){
                        Log.d("success",response.body().toString())
                    }else{
                        Log.d("error",response.errorBody()?.string().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("fail",t.message.toString())
                }
            })
        }
    }
}