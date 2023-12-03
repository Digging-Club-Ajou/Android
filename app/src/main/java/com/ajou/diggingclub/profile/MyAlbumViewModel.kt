package com.ajou.diggingclub.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajou.diggingclub.UserDataStore
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAlbumViewModel @Inject constructor() : ViewModel() {
    private val _albumInfo = MutableLiveData<ReceivedAlbumModel>()

    val albumInfo : LiveData<ReceivedAlbumModel>
    get() = _albumInfo

    private val _albumId = MutableLiveData<String>()
    val albumId : LiveData<String>
    get() = _albumId

    private val _userId = MutableLiveData<String>()
    val userId : LiveData<String>
    get() = _userId

    private val _nickname = MutableLiveData<String>()
    val nickname : LiveData<String>
    get() = _nickname

    private val _albumExist = MutableLiveData<Boolean>()

    val albumExist : LiveData<Boolean>
        get() = _albumExist

    private val _first = MutableLiveData<Boolean>()

    val first : LiveData<Boolean>
        get() = _first

    private val _moveToMelody = MutableLiveData<Boolean>()
    val moveToMelody : LiveData<Boolean>
        get() = _moveToMelody

    val dataStore = UserDataStore()
    fun getFirstFlag() = viewModelScope.launch {
        val getData = dataStore.getFirstFlag()
        _first.value = getData
    }
    fun getExistFlag() = viewModelScope.launch {
        val getData = dataStore.getAlbumExistFlag()
        _albumExist.value = getData
    }

    fun setAlbumInfo(item : ReceivedAlbumModel){
        _albumInfo.postValue(item)
    }
    fun setAlbumId(id : String){
        _albumId.postValue(id)
    }
    fun setUserId(id : String){
        _userId.postValue(id)
    }
    fun setNickname(name : String){
        _nickname.postValue(name)
    }
    fun setMoveToMelody(value : Boolean){
        _moveToMelody.postValue(value)
    }
}