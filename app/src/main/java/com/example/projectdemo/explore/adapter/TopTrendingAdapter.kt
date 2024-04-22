package com.example.projectdemo.explore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.databinding.ItemTopBinding
import com.example.projectdemo.dataclass.DataDefaultRings
import com.example.projectdemo.untils.convertDurationToTimeString

class TopTrendingAdapter(private val topTrending :List<DataDefaultRings.Data>) :
    RecyclerView.Adapter<TopTrendingAdapter.TopTrendingViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    inner class TopTrendingViewHolder(private val binding:ItemTopBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindTopTrend(data :DataDefaultRings.Data, position: Int){
            val result = convertDurationToTimeString(data.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime1.text = "$hour:$minute"
            binding.txtTitle1.text = data.name
            binding.txtTop.text = (position+1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopTrendingViewHolder {
        val binding = ItemTopBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return TopTrendingViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return topTrending.size
    }

    override fun onBindViewHolder(holder: TopTrendingViewHolder, position: Int) {
        holder.bindTopTrend(topTrending[position],position)
    }
}