package com.ajou.diggingclub.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajou.diggingclub.UserDataStore
import kotlinx.coroutines.launch

class StartViewModel : ViewModel() {

    private val _first = MutableLiveData<Boolean>()

    val first : LiveData<Boolean>
        get() = _first

    private val _exist = MutableLiveData<Boolean>()

    val exist : LiveData<Boolean>
        get() = _exist

    val dataStore = UserDataStore()

    fun checkFirstFlag() = viewModelScope.launch {
        val getData = dataStore.getFirstFlag()

        _first.value = getData
    }

    fun getExistFlag() = viewModelScope.launch {
        val getData = dataStore.getAlbumExistFlag()
        _exist.value = getData
    }

}