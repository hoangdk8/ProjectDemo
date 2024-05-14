package com.example.projectdemo.ui.explore.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemTopBinding
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.ui.home.listener.OnItemClickListener
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.logd

class TopMusicAdapter(private val topDown :List<DataDefaultRings.RingTone>
) :
    RecyclerView.Adapter<TopMusicAdapter.TopDownViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION
    inner class TopDownViewHolder(private val binding: ItemTopBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindTopDown(ringTone : DataDefaultRings.RingTone, position: Int){
            val result = convertDurationToTimeString(ringTone.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime.text = "$hour:$minute"
            binding.txtTitle.text = ringTone.name
            binding.txtTop.text = (position+1).toString()
            when(position){
                0 -> binding.txtTop.alpha = 1F
                1 -> binding.txtTop.alpha = 0.7F
                2 -> binding.txtTop.alpha = 0.5F
                else -> binding.txtTop.alpha = 0.3F

            }
            binding.imgStatus.setImageResource(R.drawable.ic_play)
            binding.imgStatus.setOnClickListener {
                selectedPosition = position
                "bindTopDown: click".logd()
            }
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