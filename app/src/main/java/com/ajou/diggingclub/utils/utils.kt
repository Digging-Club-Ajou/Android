package com.ajou.diggingclub.utils

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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

class IndentLeadingMarginSpan(
    private val indentDelimiters: List<String> = INDENT_DELIMITERS
) : LeadingMarginSpan {

    private var indentMargin: Int = 0

    override fun getLeadingMargin(first: Boolean): Int = if (first) 0 else indentMargin

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout
    ) {
        // New Line 일때만 체크
        if (!isFirstLine) {
//            Log.d("margin", "firstLine")
            return
        }

        // 해당줄의 처음 2글자를 가져옴
        val lineStartText =
            runCatching { text.substring(lineStart, lineStart + 2) }.getOrNull() ?: return
//        Log.d("margin",lineStartText)
        // 2글자중 마지막 값을 trim한게 delimiter 목록에 포함된다면 해당 길이만큼을 indentMargin 지정
        indentMargin =
            if (indentDelimiters.contains(lineStartText.trimEnd())) {
                paint.measureText(lineStartText).toInt()
            } else {
                0
            }
//        Log.d("margin",indentMargin.toString())
    }

    companion object {
        private val INDENT_DELIMITERS = listOf("·", "ㆍ", "-", "•")
    }
}

fun hideKeyboard(activity : Activity){
    if(activity.currentFocus != null){
        val inputManager : InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
