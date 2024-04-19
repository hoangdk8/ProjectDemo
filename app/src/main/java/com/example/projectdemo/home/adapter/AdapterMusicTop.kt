package com.example.projectdemo.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.dataclass.MusicBanner

class AdapterMusicTop(private val musicTopList: List<MusicBanner>) :
    RecyclerView.Adapter<AdapterMusicTop.MusicTopViewHolder>() {
    class MusicTopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_recycler_view)
        val image: ImageView = itemView.findViewById(R.id.img_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicTopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        return MusicTopViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musicTopList.size
    }

    override fun onBindViewHolder(holder: MusicTopViewHolder, position: Int) {
        val music = musicTopList[position]
        holder.title.text = music.title
        holder.image.setImageResource(music.image)
    }
}