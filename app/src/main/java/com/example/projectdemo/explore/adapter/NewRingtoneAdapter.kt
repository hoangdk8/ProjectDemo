package com.example.projectdemo.explore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.dataclass.DataDefaultRings
import com.example.projectdemo.dataclass.NewRingtone

class NewRingtoneAdapter(private val newRingtone :List<DataDefaultRings.Data>) :
    RecyclerView.Adapter<NewRingtoneAdapter.NewRingtoneViewHolder>() {
    class NewRingtoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_title_1)
        val image: ImageView = itemView.findViewById(R.id.img_music_1)
        val time: TextView = itemView.findViewById(R.id.txt_time_1)
        val top: TextView = itemView.findViewById(R.id.txt_top)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewRingtoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top, parent, false)
        return NewRingtoneViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newRingtone.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewRingtoneViewHolder, position: Int) {
        val newRingtone = newRingtone[position]
        holder.title.text = newRingtone.name
        holder.time.text = "0:"+(newRingtone.duration?.div(1000)).toString()
        holder.top.text = (position + 1).toString()
    }
}