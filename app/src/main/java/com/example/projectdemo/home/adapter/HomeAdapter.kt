package com.example.projectdemo.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ItemAdBinding
import com.example.projectdemo.databinding.ItemLoadingBinding
import com.example.projectdemo.databinding.ItemPlaylistBinding
import com.example.projectdemo.dataclass.DataDefaultRings
import com.example.projectdemo.home.interfa.OnItemClickListener
import com.example.projectdemo.untils.convertDurationToTimeString
import org.greenrobot.eventbus.EventBus


class HomeAdapter(
    private val itemList: List<Any>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var minute: String = "00"
    private var second: String = "00"
    private var selectedOldPosition = RecyclerView.NO_POSITION
    private var selectedNewPosition = RecyclerView.NO_POSITION

    companion object {
        private const val ITEM_TYPE_MUSIC = 0
        private const val ITEM_TYPE_FRAME = 1
        private const val ITEM_TYPE_LOAD_MORE = 2
    }

    inner class PlayListViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindPlayListView(data: DataDefaultRings.Data, position: Int) {
            val result = convertDurationToTimeString(data.duration!!)
            minute = result[0]
            second = result[1]
            binding.tvTitle.text = data.name
            binding.tvTime.text = "$minute:$second"
            if (position == selectedOldPosition) {
                binding.imgStatus.setImageResource(R.drawable.ic_play)
            } else if (position == selectedNewPosition) {
                binding.imgStatus.setImageResource(R.drawable.ic_pause)
            }
            binding.ctnMusic.setOnClickListener {
                selectedOldPosition = selectedNewPosition
                selectedNewPosition = position
                listener.onItemClick(
                    data.url!!,
                    data.name!!,
                    data.duration!!
                )
                notifyItemChanged(selectedOldPosition)
                notifyItemChanged(selectedNewPosition)
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

            ITEM_TYPE_LOAD_MORE -> {
                val binding = ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return LoadMoreViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        EventBus.getDefault().unregister(holder)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayListViewHolder -> {
                holder.bindPlayListView(itemList[position] as DataDefaultRings.Data,position)
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
            position == itemList.size -> ITEM_TYPE_LOAD_MORE
            else -> ITEM_TYPE_MUSIC
        }
    }
}
