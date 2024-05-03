package com.example.projectdemo.explore.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemTopBinding
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.home.listener.OnItemClickListener
import com.example.projectdemo.untils.convertDurationToTimeString

class TopMusicAdapter(private val topDown :List<DataDefaultRings.Data>,
                      private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<TopMusicAdapter.TopDownViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION
    inner class TopDownViewHolder(private val binding: ItemTopBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindTopDown(data : DataDefaultRings.Data, position: Int){
            val result = convertDurationToTimeString(data.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime1.text = "$hour:$minute"
            binding.txtTitle1.text = data.name
            binding.txtTop.text = (position+1).toString()
            when(position){
                0 -> binding.txtTop.alpha = 1F
                1 -> binding.txtTop.alpha = 0.7F
                2 -> binding.txtTop.alpha = 0.5F
                else -> binding.txtTop.alpha = 0.3F

            }
            binding.imgStatus1.setImageResource(R.drawable.ic_play)
            binding.imgStatus1.setOnClickListener {
                listener.onItemClick(
                    data.url!!,
                    data.name!!,
                    data.duration!!
                )
                selectedPosition = position
                Log.d("hoang", "bindTopDown: click")
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