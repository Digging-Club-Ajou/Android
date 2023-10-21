package com.ajou.diggingclub.intro

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.databinding.ItemIntroArtistBinding
import com.ajou.diggingclub.intro.fragments.IntroFragment2
import com.ajou.diggingclub.utils.fromDpToPx

class IntroArtistRVAdapter(val context: Context, val categoryList: List<String>, val artistInfoList : List<ArtistInfoModel>, val link : IntroFragment2.AdapterToFragment) : RecyclerView.Adapter<IntroArtistRVAdapter.ViewHolder>() {

    private val categoryLists : List<List<IntroSelectModel>> = categoryList
        .map { targetCategory ->
            artistInfoList
                .filter { it.category == targetCategory }
                .map { IntroSelectModel(it.name,null,it.imageUrl,false) }
        }
    private val adapterLink = AdapterToAdapter()
    private val adapterList: List<IntroInnerArtistRVAdapter> = List(categoryList.size) { index ->
        IntroInnerArtistRVAdapter(context, categoryLists[index], adapterLink)
    }

    inner class ViewHolder(var binding : ItemIntroArtistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : IntroInnerArtistRVAdapter, category:String) {
            binding.category.text = category
            binding.innerView.adapter = item
            val gridLayoutManager = GridLayoutManager(context,3)
            binding.innerView.addItemDecoration(IntroInnerArtistRVAdapter.GridSpacingItemDecoration(3,8f.fromDpToPx()))
            binding.innerView.layoutManager = gridLayoutManager
        }
    }

    inner class AdapterToAdapter {
        fun getSelectedItem(data : IntroSelectModel, position : Int) {
//            if(data.selected)
            Log.d("data",data.toString())
            link.getSelectedItem(data,position)
        }
    }

    fun setBlock(enabled : Boolean){
        adapterList.forEach {
            it.blockAdd(enabled)
        }
    // 여기서 innerAdapter의 enabled 값 false든 true로 변경해야함
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIntroArtistBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("categoryLists",categoryLists[position].toString())
        Log.d("category",categoryList[position])
        holder.bind(adapterList[position],categoryList[position])

    }}