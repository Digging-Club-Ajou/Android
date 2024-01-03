package com.ajou.diggingclub.ground.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.databinding.ItemSearchUserBinding
import com.ajou.diggingclub.ground.FollowDataViewModel
import com.ajou.diggingclub.ground.GroundActivity
import com.ajou.diggingclub.ground.models.FollowingModel
import com.ajou.diggingclub.profile.fragments.FollowerFragment
import com.ajou.diggingclub.utils.ManageFollow
import com.ajou.diggingclub.utils.requestOptions
import com.ajou.diggingclub.utils.setOnSingleClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowRVAdapter (val context : Context, var list : List<FollowingModel>, val memberId : String?, val userId : String?, val type : String, private val link : ManageFollow) : RecyclerView.Adapter<FollowRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding : ItemSearchUserBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : FollowingModel){
            if(memberId == userId && type == "follower"){
                binding.removeBtn.visibility = View.VISIBLE
                binding.removeBtn.isSelected = true
            }else{
                if(userId != item.memberId){
                    binding.followingBtn.visibility = View.VISIBLE
                    if(item.isFollowing == true){
                        binding.followingBtn.isSelected = true
                        binding.followingBtn.text = "팔로잉"
                    }else{
                        binding.followingBtn.isSelected = false
                        binding.followingBtn.text = "팔로우"
                    }
                }else{
                    binding.followingBtn.visibility = View.GONE
                }
                if(item.isFollower == true) binding.text.visibility = View.VISIBLE
                else binding.text.visibility = View.GONE
            }
            if(item.albumId != "0"){
                Glide.with(context)
                    .load(item.imageUrl)
                    .apply(requestOptions)
                    .into(binding.image)
            }
            binding.nickname.text = item.nickname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowRVAdapter.ViewHolder {
        val binding = ItemSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowRVAdapter.ViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
        holder.binding.followingBtn.setOnSingleClickListener {
            if(holder.binding.followingBtn.isSelected){
                holder.binding.followingBtn.isSelected = false
                holder.binding.followingBtn.text = "팔로우"
                link.getFollowId(list[position].memberId, connect = true, myFollower = false)
            }else{
                holder.binding.followingBtn.isSelected = true
                holder.binding.followingBtn.text = "팔로잉"
                link.getFollowId(list[position].memberId, connect = false, myFollower = false)
            }
        }
        holder.binding.removeBtn.setOnSingleClickListener {
            Log.d("removeBtn","clicked!")
            Log.d("memberId",list[position].memberId)
            link.getFollowId(list[position].memberId, connect = true, myFollower = true)
        //                notifyItemRemoved(position) // TODO 팔로워 추가해보고 바로 삭제되는지 확인하기
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: List<FollowingModel>) {
        list = newList
        notifyDataSetChanged()
    }

//    fun deleteFollower(position : Int){
//
//    }

}