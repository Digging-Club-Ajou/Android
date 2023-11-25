package com.ajou.diggingclub.ground

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajou.diggingclub.ground.models.FollowingModel
import com.ajou.diggingclub.network.RetrofitInstance
import com.ajou.diggingclub.network.api.FollowingService
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowDataViewModel : ViewModel() {
    private val client = RetrofitInstance.getInstance().create(FollowingService::class.java)

    private val _followingList = MutableLiveData<List<FollowingModel>>()

    val followingList : LiveData<List<FollowingModel>>
        get() = _followingList

    private val _followerList = MutableLiveData<List<FollowingModel>>()

    val followerList : LiveData<List<FollowingModel>>
        get() = _followerList

    private val _userId = MutableLiveData<String>()
    val userId : LiveData<String>
        get() = _userId


    fun setFollowingList(list : List<FollowingModel>){
        _followingList.value = list
    }

    fun setFollowerList(list : List<FollowingModel>){
        _followerList.value = list
    }

    fun setUserId(id : String){
        _userId.value = id
    }
    fun postFollowing(accessToken : String, refreshToken : String, memberId : String){
        val jsonObject = JsonObject().apply {
            addProperty("memberId", memberId)
        }
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
        client.postFollowing(accessToken,refreshToken,requestBody).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    Log.d("팔로잉 성공", "팔로잉 성공")
                }else{
                    Log.d("팔로잉 실패",response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("fail",t.message.toString())
            }
        })
    }

    fun deleteFollowing(accessToken : String, refreshToken : String, memberId : String, userId : String){
        val jsonObject = JsonObject().apply {
            addProperty("followingId", userId)
            addProperty("followedId", memberId)
        }
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
        client.deleteFollowing(accessToken, refreshToken, requestBody).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    Log.d("팔로잉 삭제 성공",response.body().toString())
                }else{
                    Log.d("팔로잉 삭제 실패 error ",response.errorBody()?.string().toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("팔로잉 삭제 실패 fail",t.message.toString())
            }
        })
    }
}