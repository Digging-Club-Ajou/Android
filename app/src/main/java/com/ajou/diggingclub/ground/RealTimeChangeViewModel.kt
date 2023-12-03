package com.ajou.diggingclub.ground

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajou.diggingclub.ground.models.NotificationsModel

class RealTimeChangeViewModel : ViewModel() {
    private val _notificationList = MutableLiveData<ArrayList<NotificationsModel>>()
    val notificationList : LiveData<ArrayList<NotificationsModel>>
        get() = _notificationList

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing : LiveData<Boolean>
        get() = _isFollowing

    fun setFollowing(isFollowing : Boolean){
        _isFollowing.value = isFollowing
    }

    fun setNotificationList(notificationList : ArrayList<NotificationsModel>){
        _notificationList.value = notificationList
    }
    fun removeNotification(position : Int){
        _notificationList.value?.removeAt(position)
    }
}