package com.ajou.diggingclub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KakaoSignUpViewModel : ViewModel() {
    private val _authCode = MutableLiveData<String>()
    val authCode : LiveData<String>
    get() = _authCode

    fun setAuthCode(string : String) = viewModelScope.launch(Dispatchers.IO) {
        if(authCode != null){
            _authCode.postValue(string)
        }
    }
}