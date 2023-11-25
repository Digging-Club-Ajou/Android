package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.databinding.ItemMusicListBinding
import com.ajou.diggingclub.ground.models.FavoritesModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class MelodyLikeRVAdapter(val context: Context, val list: List<FavoritesModel>) : RecyclerView.Adapter<MelodyLikeRVAdapter.ViewHolder>() {

    private val requestOptions = RequestOptions()
        .transform(CenterCrop(), RoundedCorners(20))
    inner class ViewHolder(private val binding: ItemMusicListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : FavoritesModel) {
            binding.title.text = item.songTitle
            binding.artist.text = item.artistName
            Glide.with(context)
                .load(item.albumCoverImageUrl)
                .apply(requestOptions)
                .into(binding.albumImg)
            binding.selectBtn.visibility = View.GONE

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
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}