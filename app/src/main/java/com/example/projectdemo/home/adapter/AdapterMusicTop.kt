package com.example.projectdemo.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemBannerBinding
import com.example.projectdemo.databinding.ItemPlaylistBinding
import com.example.projectdemo.dataclass.MusicBanner

class AdapterMusicTop(private val musicTopList: List<MusicBanner>) :
    RecyclerView.Adapter<AdapterMusicTop.MusicTopViewHolder>() {
    class MusicTopViewHolder(private val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindBannerView(data: MusicBanner){
            binding.imgBanner.setImageResource(data.image)
            binding.txtBanner.text = data.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicTopViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MusicTopViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return musicTopList.size
    }

    override fun onBindViewHolder(holder: MusicTopViewHolder, position: Int) {
        holder.bindBannerView(musicTopList[position])
    }
}