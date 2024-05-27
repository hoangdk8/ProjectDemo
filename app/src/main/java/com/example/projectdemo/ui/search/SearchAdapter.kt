package com.example.projectdemo.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.ItemSearchTrendingBinding
import com.example.projectdemo.listener.eventbus.EventSearch
import com.example.projectdemo.untils.eventBusPost
import javax.inject.Inject

class SearchAdapter @Inject constructor(
    private val itemList: List<DataDefaultRings.RingTone>
) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(private val binding: ItemSearchTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bindSearch(searchTrending: DataDefaultRings.RingTone, position: Int) {
            binding.txtTitle.text = searchTrending.name

            binding.root.setOnClickListener {
                eventBusPost(EventSearch(binding.txtTitle.text.toString()))
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchTrendingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bindSearch(itemList[position], position)
    }
}