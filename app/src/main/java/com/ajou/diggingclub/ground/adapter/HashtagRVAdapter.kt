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
        val text : TextView = view.findViewById(R.id.text)
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