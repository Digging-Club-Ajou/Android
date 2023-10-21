package com.ajou.diggingclub.intro

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.intro.fragments.IntroFragment1
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class IntroGenreRVAdapter(val context: Context, val list : List<IntroSelectModel>, val link : IntroFragment1.AdapterToFragment) : RecyclerView.Adapter<IntroGenreRVAdapter.ViewHolder>() {

    var enabled = true

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image : ImageView = view.findViewById(R.id.image)
        val text : TextView = view.findViewById(R.id.text)
        val overlay : ImageView = view.findViewById(R.id.overlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_intro_genre, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = list[position].text

        val multiOptions = MultiTransformation(
            CircleCrop(),
            RoundedCorners(10)
        ) // glide 옵션

        if(list[position].selected){
            holder.overlay.visibility = View.VISIBLE
        }else{
            holder.overlay.visibility = View.GONE
            holder.image.setColorFilter(Color.parseColor("#30BBBBBB"),PorterDuff.Mode.SRC_ATOP)
        }

        Glide.with(this.context)
            .load(list[position].image)
            .apply(RequestOptions.bitmapTransform(multiOptions))
            .into(holder.image)


        holder.image.setOnClickListener {
            if(enabled){
                list[position].selected = !list[position].selected
                link.getSelectedItem(list[position],position)
                notifyItemChanged(position)
            }
            else if(!enabled&&list[position].selected){
                list[position].selected = !list[position].selected
                link.getSelectedItem(list[position],position)
                notifyItemChanged(position)
            }
            else{
                //
                Log.d("holder","too many")
            }
            // 선택된 거 변경 뜨게
        }
    }

    fun blockAdd(bool:Boolean){
        enabled = bool
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