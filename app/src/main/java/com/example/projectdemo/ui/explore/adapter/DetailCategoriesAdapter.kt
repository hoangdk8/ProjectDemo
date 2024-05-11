package com.example.projectdemo.ui.explore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemAdBinding
import com.example.projectdemo.databinding.ItemPlaylistBinding
import com.example.projectdemo.data.dataclass.DataDetailCategories
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_ADVERTISE
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_MUSIC
import com.example.projectdemo.untils.convertDurationToTimeString

class DetailCategoriesAdapter(private val itemList: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION
    private lateinit var onClickItem: (ringtoneModel: DataDetailCategories.Data) -> Unit
    private var isPlaying = false

    inner class MusicViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindMusicView(cateRingtonesModel: DataDetailCategories.Data, position: Int) {
            val result = convertDurationToTimeString(cateRingtonesModel.duration!!)
            hour = result[0]
            minute = result[1]
            binding.tvTitle.text = cateRingtonesModel.name
            binding.tvTime.text = "$hour:$minute"
            if (isPlaying && selectedPosition == position) {
                binding.imgStatus.setImageResource(R.drawable.ic_pause)
            } else {
                binding.imgStatus.setImageResource(R.drawable.ic_play)
            }
            binding.imgStatus.setOnClickListener {
                isPlaying = !isPlaying
                onClickItem.invoke(cateRingtonesModel)
                selectedPosition = position
                notifyItemChanged(position)
            }
        }
    }

    fun onClickItemListener(onClickItem: (ringtoneModelCategory: DataDetailCategories.Data) -> Unit) {
        this.onClickItem = onClickItem
    }

//    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
//        RecyclerView.ViewHolder(binding.root)

    inner class AdversiteViewHolder(private val binding: ItemAdBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_MUSIC -> {
                val binding = ItemPlaylistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MusicViewHolder(binding)
            }

            ITEM_TYPE_ADVERTISE -> {
                val binding = ItemAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AdversiteViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicViewHolder -> {
                itemList[position].let { holder.bindMusicView(it as DataDetailCategories.Data, position) }
            }

            is AdversiteViewHolder -> {}

            // is LoadingViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position % 5 == 0 && position != 0 && position < itemList.size -> ITEM_TYPE_ADVERTISE
            else -> ITEM_TYPE_MUSIC
        }
    }
}
