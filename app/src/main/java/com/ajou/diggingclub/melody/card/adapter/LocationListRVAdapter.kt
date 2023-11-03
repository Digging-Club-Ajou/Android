package com.ajou.diggingclub.melody.card.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.diggingclub.R
import com.ajou.diggingclub.melody.card.SearchLocationFragment
import com.ajou.diggingclub.melody.models.LocationModel

class LocationListRVAdapter(val context: Context, val list : List<LocationModel>, val link : SearchLocationFragment.AdapterToFragment) : RecyclerView.Adapter<LocationListRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById(R.id.name)
        val distance : TextView = view.findViewById(R.id.distance)
        val item : RelativeLayout = view.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.distance.text = list[position].distance
        holder.name.text = list[position].name
        holder.item.setOnClickListener {
            link.getSelectedItem(list[position].name)
        }
    }
}