package com.ajou.diggingclub.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajou.diggingclub.UserDataStore
import kotlinx.coroutines.launch

class StartViewModel : ViewModel() {
    val dataStore = UserDataStore()

    private val _first = MutableLiveData<Boolean>()

    val first : LiveData<Boolean>
        get() = _first
    fun getFirstFlag() = viewModelScope.launch {
        val getData = dataStore.getFirstFlag()
        _first.value = getData
    }

}