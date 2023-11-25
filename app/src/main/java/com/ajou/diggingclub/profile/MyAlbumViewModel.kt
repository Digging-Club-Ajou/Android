package com.ajou.diggingclub.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel

class MyAlbumViewModel :ViewModel() {
    private val _albumInfo = MutableLiveData<ReceivedAlbumModel>()

    val albumInfo : LiveData<ReceivedAlbumModel>
    get() = _albumInfo

    private val _albumId = MutableLiveData<String>()
    val albumId : LiveData<String>
    get() = _albumId

    fun setAlbumInfo(item : ReceivedAlbumModel){
        _albumInfo.value = item
    }
    fun setAlbumId(id : String){
        _albumId.value = id
    }
}