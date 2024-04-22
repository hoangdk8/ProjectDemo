package com.example.projectdemo.explore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemTopBinding
import com.example.projectdemo.dataclass.DataDefaultRings
import com.example.projectdemo.untils.convertDurationToTimeString

class NewRingtoneAdapter(private val newRingtone :List<DataDefaultRings.Data>) :
    RecyclerView.Adapter<NewRingtoneAdapter.NewRingtoneViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    inner class NewRingtoneViewHolder(private val binding:ItemTopBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindNewRingtone(data :DataDefaultRings.Data,position: Int){
            val result = convertDurationToTimeString(data.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime1.text = "$hour:$minute"
            binding.txtTitle1.text = data.name
            binding.txtTop.text = (position+1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewRingtoneViewHolder {
        val binding = ItemTopBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return NewRingtoneViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return newRingtone.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewRingtoneViewHolder, position: Int) {
        holder.bindNewRingtone(newRingtone[position],position)
    }
}