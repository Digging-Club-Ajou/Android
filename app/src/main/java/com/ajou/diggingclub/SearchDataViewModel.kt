package com.ajou.diggingclub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajou.diggingclub.melody.models.LocationModel
import com.ajou.diggingclub.melody.models.MusicSpotifyModel

class SearchDataViewModel: ViewModel() {
    private val _list = MutableLiveData<ArrayList<MusicSpotifyModel>>()
    val list: LiveData<ArrayList<MusicSpotifyModel>>
        get() = _list

    private val _locationList = MutableLiveData<ArrayList<LocationModel>>()
    val locationList: LiveData<ArrayList<LocationModel>>
        get() = _locationList

    fun addSearchList(newList: List<MusicSpotifyModel>){
        _list.value?.addAll(newList)
    }

    fun setEmptyList(){
        _list.value = arrayListOf()
        _locationList.value = arrayListOf()
    }

    fun addLocationList(newList: List<LocationModel>){
        _locationList.value?.addAll(newList)
    }
}