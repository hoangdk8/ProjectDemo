package com.example.projectdemo.explore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.databinding.ItemTopBinding
import com.example.projectdemo.dataclass.DataDefaultRings
import com.example.projectdemo.untils.convertDurationToTimeString

class TopMusicAdapter(private val topDown :List<DataDefaultRings.Data>) :
    RecyclerView.Adapter<TopMusicAdapter.TopDownViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    inner class TopDownViewHolder(private val binding: ItemTopBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindTopDown(data :DataDefaultRings.Data, position: Int){
            val result = convertDurationToTimeString(data.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime1.text = "$hour:$minute"
            binding.txtTitle1.text = data.name
            binding.txtTop.text = (position+1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopDownViewHolder {
        val binding = ItemTopBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return TopDownViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return topDown.size
    }

    override fun onBindViewHolder(holder: TopDownViewHolder, position: Int) {
        holder.bindTopDown(topDown[position],position)
    }
}