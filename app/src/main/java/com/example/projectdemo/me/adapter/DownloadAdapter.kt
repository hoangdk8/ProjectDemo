package com.example.projectdemo.me.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.ItemDownloadBinding
import com.example.projectdemo.untils.convertDurationToTimeString

class DownloadAdapter (private val download :List<DataDefaultRings.Data>
) :
RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION
    inner class DownloadViewHolder(private val binding: ItemDownloadBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindDownoad(data : DataDefaultRings.Data, position: Int){
            val result = convertDurationToTimeString(data.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime.text = "$hour:$minute"
            binding.txtTitle.text = data.name
            binding.imgStatus.setImageResource(R.drawable.ic_play)
            binding.imgStatus.setOnClickListener {
//                listener.onItemClick(
//                    data.url!!,
//                    data.name!!,
//                    data.duration!!
//                )
                selectedPosition = position
            }
            binding.imgDelete.setOnClickListener {
                // Notify the adapter that an item has been removed
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val binding = ItemDownloadBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return DownloadViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return download.size
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bindDownoad(download[position],position)
    }
}