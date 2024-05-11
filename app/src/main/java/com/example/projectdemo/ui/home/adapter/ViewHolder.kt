package com.example.projectdemo.ui.home.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.databinding.EachItemBinding
import com.example.projectdemo.data.dataclass.DataItemType
import com.example.projectdemo.data.dataclass.MusicBanner

class HorizontalViewHolder(private var binding: EachItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.childRecyclerView.setHasFixedSize(true)
        binding.childRecyclerView.layoutManager =
            LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
    }

    fun bind(recyclerItemList: List<MusicBanner>) {
        val adapter = HorizontalListAdapter(DataItemType.ITEM_TYPE_BANNER, recyclerItemList)
        binding.childRecyclerView.adapter = adapter
    }
}
