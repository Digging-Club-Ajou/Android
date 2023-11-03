package com.ajou.diggingclub.melody.card.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.melody.card.SearchMusicFragment
import com.ajou.diggingclub.melody.models.MusicSpotifyModel
import com.bumptech.glide.Glide

class MusicListRVAdapter(val context: Context, val list : List<MusicSpotifyModel>, val link : SearchMusicFragment.AdapterToFragment) : RecyclerView.Adapter<MusicListRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val btn : ImageView = view.findViewById(R.id.selectBtn)
        val albumImg : ImageView = view.findViewById(R.id.albumImg)
        val title : TextView = view.findViewById(R.id.title)
        val artist : TextView = view.findViewById(R.id.artist)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.btn.setOnClickListener {
            link.getSelectedItem(list[position])
        }
        holder.artist.text = list[position].artist
        holder.title.text = list[position].title

        Glide.with(this.context)
            .load(list[position].imageUrl)
//            .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
//            .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
//            .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
            .into(holder.albumImg)
    }
}