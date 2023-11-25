package com.ajou.diggingclub.utils

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.ground.fragments.GroundFragmentDirections
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel

interface AdapterToFragment {
        fun getSelectedItem(item : ReceivedAlbumModel)

        fun getSelectedId(memberId : String, albumId : String, name : String, type : String)

        fun postFavoriteId(melodyCardId : String, isLike : Boolean)

        fun addRecordCount(position : Int)
}
