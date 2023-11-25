package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.fragments.NotificationFragment
import com.ajou.diggingclub.ground.models.NotificationsModel
import com.ajou.diggingclub.utils.setOnSingleClickListener

class NotificationRVAdapter(val context: Context, val list: List<NotificationsModel>, val link : NotificationFragment.DeleteNotification) : RecyclerView.Adapter<NotificationRVAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val notification : TextView = view.findViewById(R.id.notification)
        val removeBtn : ImageView = view.findViewById(R.id.removeBtn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationRVAdapter.ViewHolder, position: Int) {
        holder.removeBtn.setOnSingleClickListener {
            link.deleteNotification(list[position].notificationId.toString())
        }
        val text = list[position].message+" "+list[position].minutes
        holder.notification.text = text
        val spannable = SpannableStringBuilder(text)
        val minutesLength = list[position].minutes.length
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context,R.color.descriptionColor)), text.length-minutesLength, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // TextView에 Spannable 문자열 설정
        holder.notification.text = spannable
    }

    override fun getItemCount(): Int {
        return list.size
    }

}