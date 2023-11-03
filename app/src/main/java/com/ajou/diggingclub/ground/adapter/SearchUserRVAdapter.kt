package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.models.MemberSearchModel
import com.bumptech.glide.Glide

class SearchUserRVAdapter(val context: Context, val list : List<MemberSearchModel>) : RecyclerView.Adapter<SearchUserRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nickname : TextView = view.findViewById(R.id.nickname)
        val image : ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nickname.text = list[position].nickname
        Glide.with(context)
            .load(list[position].imageUrl)
            .into(holder.image)
    }
}