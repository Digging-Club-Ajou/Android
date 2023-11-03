package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.bumptech.glide.Glide

class MelodyCardRVAdapter(val context: Context, val list:List<ReceivedMelodyCardModel>) : RecyclerView.Adapter<MelodyCardRVAdapter.ViewHolder>() {

    inner class ViewHolder(val view:View): RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.image)
        val title : TextView = view.findViewById(R.id.title)
        val artist : TextView = view.findViewById(R.id.artist)
        val cardDescription : TextView = view.findViewById(R.id.cardDescription)
        val location : TextView = view.findViewById(R.id.location)
        val nickname : TextView = view.findViewById(R.id.nickname)
        val playIcon : ImageView = view.findViewById(R.id.playIcon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MelodyCardRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_ground,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MelodyCardRVAdapter.ViewHolder, position: Int) {
        val layerDrawable = ContextCompat.getDrawable(context, R.drawable.playicon)?.mutate() as LayerDrawable
        holder.title.text = list[position].songTitle
        holder.artist.text = list[position].artistName
        holder.nickname.text = list[position].nickname
        layerDrawable.getDrawable(0).setColorFilter(android.graphics.Color.parseColor(list[position].color), PorterDuff.Mode.SRC_IN)
        holder.playIcon.setImageDrawable(layerDrawable)
        if(list[position].cardDescription!=null){
            holder.cardDescription.text = list[position].cardDescription
        }else{
            holder.cardDescription.visibility = View.GONE
        }
        if(list[position].imageUrl!=null){
            Glide.with(context)
                .load(list[position].imageUrl)
                .into(holder.image)
        }else{
            holder.image.setBackgroundColor(list[position].color.toInt())
        }
        if(list[position].address!=null){
            holder.location.text = list[position].address
        }else{
            holder.location.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}