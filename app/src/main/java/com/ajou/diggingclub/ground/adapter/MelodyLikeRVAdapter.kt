package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.databinding.ItemMusicListBinding
import com.bumptech.glide.Glide

class MelodyLikeRVAdapter(val context: Context, val list: List<String>) : RecyclerView.Adapter<MelodyLikeRVAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemMusicListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(image : String, title : String, artist : String) {
            binding.title.text = title
            binding.artist.text = artist
            Glide.with(context)
                .load(image)
                .into(binding.albumImg)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MelodyLikeRVAdapter.ViewHolder {
        val binding = ItemMusicListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MelodyLikeRVAdapter.ViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data, data, data)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}