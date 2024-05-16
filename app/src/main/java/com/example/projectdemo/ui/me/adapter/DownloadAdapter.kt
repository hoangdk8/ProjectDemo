package com.example.projectdemo.ui.me.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.ItemDownloadBinding
import com.example.projectdemo.listener.eventbus.EventUnDownload
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusPost

class DownloadAdapter(
    private val download: List<DataDefaultRings.RingTone>
) :
    RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class DownloadViewHolder(private val binding: ItemDownloadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bindDownoad(ringTone: DataDefaultRings.RingTone, position: Int) {
            val result = convertDurationToTimeString(ringTone.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime.text = "$hour:$minute"
            binding.txtTitle.text = ringTone.name
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
                eventBusPost(EventUnDownload(ringTone.id!!))
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val binding = ItemDownloadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DownloadViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return download.size
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bindDownoad(download[position], position)
    }
}