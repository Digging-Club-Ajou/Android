package com.ajou.diggingclub.utils

import android.app.Activity
import java.io.IOException

fun getJsonDataFromAssets(activity : Activity) : String? {
    val jsonString : String
    try {
        jsonString = activity.assets.open("artistInfo.json")
            .bufferedReader()
            .use { it.readText() }
    }
    catch (ioException : IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}