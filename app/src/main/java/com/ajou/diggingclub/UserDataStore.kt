package com.ajou.diggingclub

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "userData")

class UserDataStore() {
    private val context = App.context()
    private val dataStore : DataStore<Preferences> = context.dataStore

    private object PreferencesKeys {
        val FIRST_FLAG = booleanPreferencesKey("first_flag")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val NICKNAME = stringPreferencesKey("nickname")
        val ALBUM_EXIST = booleanPreferencesKey("album_exist")
        val MEMBER_ID = stringPreferencesKey("member_id")
        val ALBUM_ID = stringPreferencesKey("album_id")
    }

    suspend fun saveAccessToken(token : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.ACCESS_TOKEN] = token
            }
        }
    }

    suspend fun getAccessToken():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
        }
    }

    suspend fun saveNickname(nickname : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.NICKNAME] = nickname
            }
        }
    }

    suspend fun getNickname():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.NICKNAME]
        }
    }

    suspend fun saveRefreshToken(token : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.REFRESH_TOKEN] = token
            }
        }
    }

    suspend fun getRefreshToken():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.REFRESH_TOKEN]
        }
    }

    suspend fun saveFirstFlag(flag : Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.FIRST_FLAG] = flag
            }
        }
    }

    suspend fun getFirstFlag():Boolean {
        var flag = false
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                flag = pref[PreferencesKeys.FIRST_FLAG] ?: false
            }
        }
        return flag
    }

    suspend fun saveAlbumExistFlag(flag : Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref[PreferencesKeys.ALBUM_EXIST] = flag
            }
        }
    }

    suspend fun getAlbumExistFlag():Boolean {
        var flag = false
            dataStore.edit { pref ->
                flag = pref[PreferencesKeys.ALBUM_EXIST] ?: false
        }
        return flag
    }

    suspend fun saveMemberId(id : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.MEMBER_ID] = id
            }
        }
    }

    suspend fun getMemberId():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.MEMBER_ID]
        }
    }
    suspend fun saveAlbumId(id : String) {
        withContext(Dispatchers.IO){
            dataStore.edit { pref ->
                pref[PreferencesKeys.ALBUM_ID] = id
            }
        }
    }

    suspend fun getAlbumId():String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.first()[PreferencesKeys.ALBUM_ID]
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dataStore.edit { pref ->
                pref.clear()
            }
        }
    }

}


