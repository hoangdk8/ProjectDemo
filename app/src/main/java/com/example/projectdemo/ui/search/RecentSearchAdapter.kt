package com.example.projectdemo.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.RecentSearch
import com.example.projectdemo.databinding.ItemSearchTrendingBinding
import com.example.projectdemo.listener.eventbus.EventSearch
import com.example.projectdemo.untils.eventBusPost
import javax.inject.Inject

class RecentSearchAdapter @Inject constructor(
    private val itemList: List<RecentSearch>
) :
    RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    inner class RecentSearchViewHolder(private val binding: ItemSearchTrendingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bindSearch(recentSearch: RecentSearch, position: Int) {
            binding.txtTitle.text = recentSearch.itemName

            binding.root.setOnClickListener {
                eventBusPost(EventSearch(binding.txtTitle.text.toString()))
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = ItemSearchTrendingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecentSearchViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bindSearch(itemList[position], position)
    }
}