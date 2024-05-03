package com.example.projectdemo.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.data.dataclass.MusicBanner
import com.example.projectdemo.databinding.ItemBannerBinding

class HorizontalListAdapter(private val viewType: Int, private val itemList: List<MusicBanner>) :
    RecyclerView.Adapter<HorizontalListAdapter.ViewHolder>() {

    inner class ViewHolder(private val bindingBanner: ItemBannerBinding) :
        RecyclerView.ViewHolder(bindingBanner.root) {
        fun bindBestSellerView(recyclerItem: MusicBanner) {
            bindingBanner.imgBanner.setImageResource(recyclerItem.image)
            bindingBanner.txtBanner.text = recyclerItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindBestSellerView(recyclerItem = itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}