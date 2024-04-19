package com.example.projectdemo.explore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectdemo.R
import com.example.projectdemo.dataclass.Categories
import com.example.projectdemo.dataclass.DataCategories

class ExploreAdapter (private val context: Context, private val categories: List<DataCategories.Data>) :
    RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder>() {
    class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_category)
        val image: ImageView = itemView.findViewById(R.id.img_categories)
        val count: TextView = itemView.findViewById(R.id.txt_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
        return ExploreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        val categories = categories[position]
        val baseImageUrl = "https://pub-a59f0b5c0b134cdb808fe708183c7d0e.r2.dev/ringstorage/categories/fr2019tkv2/"
        holder.title.text = categories.name
        Glide.with(context).load(baseImageUrl+categories.url).into(holder.image)

        holder.count.text = categories.count.toString() + " ringtones"
    }
}