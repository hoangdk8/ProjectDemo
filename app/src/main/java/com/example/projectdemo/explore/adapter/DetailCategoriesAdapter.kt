package com.example.projectdemo.explore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemAdBinding
import com.example.projectdemo.databinding.ItemLoadingBinding
import com.example.projectdemo.databinding.ItemPlaylistBinding
import com.example.projectdemo.dataclass.DataDefaultRings
import com.example.projectdemo.dataclass.DataDetailCategories
import com.example.projectdemo.home.interfa.OnItemClickListener
import com.example.projectdemo.untils.convertDurationToTimeString

class DetailCategoriesAdapter(
private val itemList: List<Any>,
private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION

    companion object {
        private const val ITEM_TYPE_MUSIC = 0
        private const val ITEM_TYPE_FRAME = 1
    }

    inner class PlayListViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindPlayListView(data: DataDetailCategories.Data, position: Int) {
            val result = convertDurationToTimeString(data.duration!!)
            hour = result[0]
            minute = result[1]
            binding.tvTitle.text = data.name
            binding.tvTime.text = "$hour:$minute"
            binding.imgStatus.setImageResource(R.drawable.ic_play)
            binding.imgStatus.setOnClickListener {
                listener.onItemClick(
                    data.url!!,
                    data.name!!,
                    data.duration!!
                )
                selectedPosition = position
            }

        }
    }

    inner class FrameViewHolder(binding: ItemAdBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LoadMoreViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_TYPE_MUSIC -> {
                val binding = ItemPlaylistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PlayListViewHolder(binding)
            }

            ITEM_TYPE_FRAME -> {
                val binding = ItemAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FrameViewHolder(binding)
            }


            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayListViewHolder -> {
                holder.bindPlayListView(itemList[position] as DataDetailCategories.Data,position)
            }

            is FrameViewHolder -> {

            }

            is LoadMoreViewHolder -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position % 5 == 0 && position != 0 && position < itemList.size -> ITEM_TYPE_FRAME
            else -> ITEM_TYPE_MUSIC
        }
    }
}
