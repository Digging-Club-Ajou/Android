package com.ajou.diggingclub.intro

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IntroViewModel : ViewModel(){
    private val _genreArr = MutableLiveData<MutableList<Int>>()
    val genreArr : LiveData<MutableList<Int>>
        get() = _genreArr

    private val _artistArr = MutableLiveData<MutableList<Int>>()
    val artistArr : LiveData<MutableList<Int>>
        get() = _artistArr

    init {
        // 초기에 빈 리스트로 초기화
        _genreArr.value = mutableListOf()
        _artistArr.value = mutableListOf()
    }

    fun addGenreArr(value: Int){
        val currentList = _genreArr.value ?: mutableListOf()
        currentList.add(value)
        _genreArr.value = currentList
        Log.d("arr",genreArr.value.toString())
    }
    fun removeGenreArr(value: Int){
        val currentList = _genreArr.value ?: mutableListOf()
        currentList.remove(value)
        _genreArr.value = currentList
        Log.d("removeArr",genreArr.value.toString())
    }
    fun setEmptyGenreArr(){
        _genreArr.value = mutableListOf()
    }
    fun setEmptyArtistArr(){
        _artistArr.value = mutableListOf()
    }
    fun getArr() : MutableList<Int>{
        if(_genreArr.value?.size==5){

        }
        return _genreArr.value ?: mutableListOf()
    }

    fun addArtistArr(value: Int){
        val currentList = _artistArr.value ?: mutableListOf()
        currentList.add(value)
        _artistArr.value = currentList
        Log.d("arr",artistArr.value.toString())
    }

    fun removeArtistArr(value: Int){
        val currentList = _artistArr.value ?: mutableListOf()
        currentList.remove(value)
        _artistArr.value = currentList
        Log.d("removeArr",artistArr.value.toString())
    }
}