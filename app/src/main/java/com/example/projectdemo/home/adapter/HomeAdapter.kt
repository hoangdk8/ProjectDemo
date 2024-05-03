package com.example.projectdemo.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemAdBinding
import com.example.projectdemo.databinding.ItemLoadingBinding
import com.example.projectdemo.databinding.ItemPlaylistBinding
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.DataItem
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_ADVERTISE
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_LOADING
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_MUSIC
import com.example.projectdemo.data.dataclass.MusicBanner
import com.example.projectdemo.databinding.EachItemBinding
import com.example.projectdemo.untils.convertDurationToTimeString


class HomeAdapter(
    private var itemList: List<DataItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedOldPosition = RecyclerView.NO_POSITION
    private var selectedNewPosition = RecyclerView.NO_POSITION
    private lateinit var onClickItem: (ringtoneModel: DataDefaultRings.Data) -> Unit

    fun onClickItemListener(onClickItem: (homeRingtonesModel: DataDefaultRings.Data) -> Unit) {
        this.onClickItem = onClickItem
    }

    inner class MusicViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindMusicView(homeRingtonesModel: DataDefaultRings.Data, position: Int) {
            val result = convertDurationToTimeString(homeRingtonesModel.duration!!)
            hour = result[0]
            minute = result[1]
            binding.tvTitle.text = homeRingtonesModel.name
            binding.tvTime.text = "$hour:$minute"
            if (position == selectedOldPosition) {
                binding.imgStatus.setImageResource(R.drawable.ic_play)
            } else if (position == selectedNewPosition) {
                binding.imgStatus.setImageResource(R.drawable.ic_pause)
            }
            binding.imgStatus.setOnClickListener {
                onClickItem.invoke(homeRingtonesModel)
                selectedOldPosition = selectedNewPosition
                selectedNewPosition = position
                notifyItemChanged(selectedOldPosition)
                notifyItemChanged(selectedNewPosition)
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class AdversiteViewHolder(private val binding: ItemAdBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_playlist -> {
                val binding =
                    ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MusicViewHolder(binding)
            }

            R.layout.item_ad -> {
                val binding =
                    ItemAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdversiteViewHolder(binding)
            }

            R.layout.item_loading -> {
                val binding =
                    ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }

            else -> {
                val binding =
                    EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HorizontalViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicViewHolder -> {
                itemList[position].bannerList.let {
                    holder.bindMusicView(it as DataDefaultRings.Data, position)
                }
            }

            is HorizontalViewHolder -> {
                itemList[position].recyclerItemList.let { holder.bind(it as List<MusicBanner>) }
            }

            is AdversiteViewHolder -> {}

            is LoadingViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].viewType) {
            ITEM_TYPE_MUSIC -> R.layout.item_playlist
            ITEM_TYPE_LOADING -> R.layout.item_loading
            ITEM_TYPE_ADVERTISE -> R.layout.item_ad
            else -> R.layout.each_item
        }
    }

}
