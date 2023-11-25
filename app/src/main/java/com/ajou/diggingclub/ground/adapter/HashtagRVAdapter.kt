package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R

class HashtagRVAdapter(val context: Context, val list:List<String>) : RecyclerView.Adapter<HashtagRVAdapter.ViewHolder>() {

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val text : TextView = view.findViewById(R.id.hashtag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashtagRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hashtag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HashtagRVAdapter.ViewHolder, position: Int) {
        val tmp = "#"+list[position]
        holder.text.text = tmp
    }

    override fun getItemCount(): Int {
        return list.size
    }
}