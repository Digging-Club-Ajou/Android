package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.fragments.SearchUserFragment
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.utils.requestOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class SearchUserRVAdapter(val context: Context, val list : List<ReceivedAlbumModel>, val link : SearchUserFragment.AdapterToFragment) : RecyclerView.Adapter<SearchUserRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nickname : TextView = view.findViewById(R.id.nickname)
        val image : ImageView = view.findViewById(R.id.image)
        val item : ConstraintLayout = view.findViewById(R.id.item)
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
            .apply(requestOptions)
            .into(holder.image)
        holder.item.setOnSingleClickListener {
            link.getSelectedItem(position)
        }
    }
}