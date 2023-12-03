package com.ajou.diggingclub.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditAlbumInfoViewModel : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _uri = MutableLiveData<Uri?>()
    val uri: LiveData<Uri?>
        get() = _uri

    fun setTitle(title: String) {
        _title.postValue(title)
    }

    fun setUri(newUri: Uri) {
        _uri.postValue(newUri)
    }

    fun setEmpty(){
        _title.value = ""
        _uri.value = null
        Log.d("uri is",uri.value.toString())
    }
}