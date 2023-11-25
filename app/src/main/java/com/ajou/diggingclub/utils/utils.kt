package com.ajou.diggingclub.utils

import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ajou.diggingclub.ground.fragments.GroundFragmentDirections
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel

class OnSingleClickListener(
    private var interval: Int = 600,
    private var onSingleClick: (View) -> Unit
) : View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        if ((elapsedRealtime - lastClickTime) < interval) {
            return
        }
        lastClickTime = elapsedRealtime
        onSingleClick(v)
    }

}

fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
    val oneClick = OnSingleClickListener {
        onSingleClick(it)
    }
    setOnClickListener(oneClick)
}