package com.ajou.diggingclub.utils

import android.app.Activity
import android.content.res.Resources
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private fun absolutelyPath(path: Uri?, activity:Activity ): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = activity.contentResolver?.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)
        Log.d("result",result.toString())
        return result!!
    } // 절대경로로 변환하는 함수

fun getMultipartFile(imageUri: Uri, activity: Activity, key: String): MultipartBody.Part {
        val file = File(absolutelyPath(imageUri, activity)) // path 동일
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val name = file.name
        return MultipartBody.Part.createFormData(key, name, requestFile)
    }
fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

val requestOptions = RequestOptions()
        .transform(CenterCrop(), RoundedCorners(20))

val multiOptions = RequestOptions().transform(
        CenterCrop(),
        RoundedCorners(40)
)