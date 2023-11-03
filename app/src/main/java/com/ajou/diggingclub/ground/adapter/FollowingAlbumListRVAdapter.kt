package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.bumptech.glide.Glide

class FollowingAlbumListRVAdapter(val context: Context, val list : List<ReceivedAlbumModel>,val type:String): RecyclerView.Adapter<FollowingAlbumListRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.image)
        val nickname : TextView = view.findViewById(R.id.nickname)
        val albumTitle : TextView = view.findViewById(R.id.albumTitle)
        val artistNames : TextView = view.findViewById(R.id.artistNames)
        val hashtag1 : TextView = view.findViewById(R.id.hashtag1)
        val hashtag2 : TextView = view.findViewById(R.id.hashtag2)
        val hashtag3 : TextView = view.findViewById(R.id.hashtag3)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowingAlbumListRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommend_album,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingAlbumListRVAdapter.ViewHolder, position: Int) {
        holder.nickname.text = list[position].nickname
        holder.albumTitle.text = list[position].albumName
        Glide.with(context)
            .load(list[position].imageUrl)
            .into(holder.image)
        val artistNamesString = list.joinToString(", ") { it.artistNames.joinToString(", ") }
        holder.artistNames.text = artistNamesString
        if(type!="following"){
            // 해시태그 받으면 값 넣어서 띄우기
        }else{
            holder.hashtag1.visibility = View.GONE
            holder.hashtag2.visibility = View.GONE
            holder.hashtag3.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal class GridSpacingItemDecoration(
        private val spanCount: Int, // Grid의 column 수
        private val spacing: Int // 간격
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount + 1      // 1부터 시작

            /** 첫번째 행(row-1)에 있는 아이템인 경우 상단에 [space] 만큼의 여백을 추가한다 */
            if (position < spanCount){
                outRect.top = spacing
            }
            /** 마지막 열(column-N)에 있는 아이템인 경우 우측에 [space] 만큼의 여백을 추가한다 */
            if (column == spanCount){
                outRect.right = spacing
            }
            /** 모든 아이템의 좌측과 하단에 [space] 만큼의 여백을 추가한다. */
            outRect.left = spacing/2
            outRect.bottom = spacing*2

        }
    }
}