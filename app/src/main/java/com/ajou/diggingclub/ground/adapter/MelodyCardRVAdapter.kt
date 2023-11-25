package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.fragments.GroundFragment
import com.ajou.diggingclub.ground.models.ReceivedMelodyCardModel
import com.ajou.diggingclub.utils.AdapterToFragment
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import java.io.IOException

class MelodyCardRVAdapter(val context: Context, val list:List<ReceivedMelodyCardModel>, private val link : AdapterToFragment, val type : String) : RecyclerView.Adapter<MelodyCardRVAdapter.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var pausedPosition: Int = 0

    inner class ViewHolder(val view:View): RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.image)
        val title : TextView = view.findViewById(R.id.title)
        val artist : TextView = view.findViewById(R.id.artist)
        val cardDescription : TextView = view.findViewById(R.id.cardDescription)
        val location : TextView = view.findViewById(R.id.location)
        val locationIcon : ImageView = view.findViewById(R.id.locationIcon)
        val nickname : TextView = view.findViewById(R.id.nickname)
        val playIcon : ImageView = view.findViewById(R.id.playIcon)
        val profileIcon : ImageView = view.findViewById(R.id.profileIcon)
        val likeBtn : ImageView = view.findViewById(R.id.likeBtn)
    }

    override fun getItemViewType(position: Int): Int {
        return when(type){
            "viewpager" -> 0
            "rv" -> 1
            else -> 2
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MelodyCardRVAdapter.ViewHolder {
        if(viewType == 0){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_viewpager,parent,false)
            return ViewHolder(view)
        }else if(viewType == 1){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_ground,parent,false)
            return ViewHolder(view)
        }else{
            // TODO 에러 처리여야 할 듯
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_ground,parent,false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MelodyCardRVAdapter.ViewHolder, position: Int) {

        val playLayerDrawable = ContextCompat.getDrawable(context, R.drawable.playicon)?.mutate() as LayerDrawable
        val playingLayerDrawable = ContextCompat.getDrawable(context, R.drawable.playingicon)?.mutate() as LayerDrawable
        holder.title.text = list[position].songTitle
        holder.artist.text = list[position].artistName
        holder.nickname.text = list[position].nickname
        playLayerDrawable.getDrawable(0).setColorFilter(android.graphics.Color.parseColor(list[position].color), PorterDuff.Mode.SRC_IN)
        playingLayerDrawable.getDrawable(0).setColorFilter(android.graphics.Color.parseColor(list[position].color), PorterDuff.Mode.SRC_IN)

        if(list[position].cardDescription!=null){
            holder.cardDescription.text = list[position].cardDescription
        }else holder.cardDescription.visibility = View.GONE

        if(list[position].imageUrl!=null){
            Glide.with(context)
                .load(list[position].imageUrl)
                .centerCrop()
                .into(holder.image)
        }else {
            Glide.with(context)
                .clear(holder.image)
            holder.image.setBackgroundColor(Color.parseColor(list[position].color))
        }

        if(list[position].address!=null){
            holder.location.text = list[position].address
        }else {
            holder.location.visibility = View.GONE
            holder.locationIcon.visibility = View.GONE
        }

        if(list[position].isPlaying) holder.playIcon.setImageDrawable(playingLayerDrawable)
        else holder.playIcon.setImageDrawable(playLayerDrawable)

        holder.likeBtn.isSelected = list[position].isLike
        holder.nickname.setOnSingleClickListener {
            link.getSelectedId(list[position].memberId.toString(),list[position].albumId.toString(), "", "card")
        }

        holder.playIcon.setOnSingleClickListener {
            if (isMediaPlayerPlaying()) {
                list[position].isPlaying = false
                stopMediaPlayer(position)
                holder.playIcon.setImageDrawable(playLayerDrawable)
            } else {
                list[position].isPlaying = true
                initializeAndPlayMediaPlayer(list[position].previewUrl,position)
                holder.playIcon.setImageDrawable(playingLayerDrawable)
                link.addRecordCount(position)
            }
        }

        holder.likeBtn.setOnSingleClickListener {
            if(holder.likeBtn.isSelected){
                holder.likeBtn.isSelected = false
                link.postFavoriteId(list[position].melodyCardId.toString(),false)
            }else{
                holder.likeBtn.isSelected = true
                link.postFavoriteId(list[position].melodyCardId.toString(),true)
            }
        }

        holder.profileIcon.setOnClickListener {
            link.getSelectedId(list[position].memberId.toString(),list[position].albumId.toString(), "", "card")
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun isMediaPlayerPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    private fun initializeAndPlayMediaPlayer(url: String, position: Int) {
        mediaPlayer = mediaPlayer  ?: MediaPlayer()

        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.seekTo(pausedPosition)
            mediaPlayer?.start()
        }

        mediaPlayer?.setOnCompletionListener {
            list[position].isPlaying = false
            notifyItemChanged(position)
        }
    }

    fun stopMediaPlayer(position: Int) {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
            }
            pausedPosition = currentPosition
        }
    }
}