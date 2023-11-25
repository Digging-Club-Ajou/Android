package com.ajou.diggingclub.ground.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.ground.models.ReceivedAlbumModel
import com.ajou.diggingclub.utils.AdapterToFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class FollowingAlbumListRVAdapter(val context: Context, val list : List<ReceivedAlbumModel>,val type:String, val link : AdapterToFragment): RecyclerView.Adapter<FollowingAlbumListRVAdapter.ViewHolder>() {

    private val requestOptions = RequestOptions()
        .transform(CenterCrop(), RoundedCorners(20))

    inner class ViewHolder(view : View):RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.image)
        val nickname : TextView = view.findViewById(R.id.nickname)
        val albumTitle : TextView = view.findViewById(R.id.albumTitle)
        val item : ConstraintLayout = view.findViewById(R.id.item)
        val profileIcon : ImageView = view.findViewById(R.id.profileIcon)
        val hashtagRV : RecyclerView = view.findViewById(R.id.hashtagRV)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowingAlbumListRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommend_album,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingAlbumListRVAdapter.ViewHolder, position: Int) {
        val artistList = list[position].artistNames.take(4)
        holder.nickname.text = list[position].nickname
        holder.albumTitle.text = list[position].albumName
        Glide.with(context)
            .load(list[position].imageUrl)
            .apply(requestOptions)
            .into(holder.image)

        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        holder.hashtagRV.adapter = HashtagRVAdapter(context, artistList)
        holder.hashtagRV.layoutManager = layoutManager

        holder.item.setOnClickListener {
            link.getSelectedId(list[position].memberId.toString(), list[position].albumId.toString(), list[position].albumName,"album")
        }
        holder.profileIcon.setOnClickListener {
            link.getSelectedItem(list[position])
        }
        holder.nickname.setOnClickListener {
            link.getSelectedItem(list[position])
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