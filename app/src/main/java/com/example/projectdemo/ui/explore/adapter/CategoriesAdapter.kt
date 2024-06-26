package com.example.projectdemo.ui.explore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectdemo.databinding.ItemCategoriesBinding
import com.example.projectdemo.data.dataclass.BASE_URL_IMAGE
import com.example.projectdemo.data.dataclass.DataCategories
import com.example.projectdemo.ui.explore.listener.OnClickCategoriesListener

class CategoriesAdapter(
    private val context: Context,
    private val categories: List<DataCategories.Data>,
    private val listener: OnClickCategoriesListener
) :
    RecyclerView.Adapter<CategoriesAdapter.ExploreViewHolder>() {
   inner class ExploreViewHolder(private val binding:ItemCategoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindCategoriesView(data: DataCategories.Data, position: Int){

            binding.txtCategory.text = data.name
            Glide.with(context).load(BASE_URL_IMAGE + data.url).into(binding.imgCategories)

            binding.txtCount.text = data.count.toString() + " ringtones"
            binding.imgCategories.setOnClickListener{
                listener.onItemClick(data.id!!, data.name!!, data.count!!,data.url!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val binding = ItemCategoriesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExploreViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        holder.bindCategoriesView(categories[position],position)
    }
}