package com.ajou.diggingclub.ground.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowingModel(
    @SerializedName("memberId") val memberId : String,
    @SerializedName("albumId") val albumId : String?,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("isFollowing") val isFollowing : Boolean?,
    @SerializedName("isFollower") val isFollower : Boolean?,
    var imageUrl : String = ""
) : Parcelable

