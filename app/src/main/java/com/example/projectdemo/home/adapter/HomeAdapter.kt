package com.example.projectdemo.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.dataclass.DataDefaultRings

interface OnItemClickListener {
    fun onItemClick(url: String,title: String,time: Int)
}
class HomeAdapter(private val context: Context,
                  private val itemList: List<Any>,
                  private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val ITEM_TYPE_MUSIC = 0
        private const val ITEM_TYPE_FRAME = 1
        private const val ITEM_TYPE_LOAD_MORE = 2
    }

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val textTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    inner class FrameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loading : ProgressBar = itemView.findViewById(R.id.loading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_MUSIC -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false)
                MusicViewHolder(view)
            }
            ITEM_TYPE_FRAME -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_ad, parent, false)
                FrameViewHolder(view)
            }
            ITEM_TYPE_LOAD_MORE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false) // Layout cho item load more
                LoadMoreViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicViewHolder -> {
                val currentItem = itemList[position] as DataDefaultRings.Data
                holder.textTitle.text = currentItem.name
                holder.textTime.text = "0:"+(currentItem.duration?.div(1000)).toString()
                holder.itemView.setOnClickListener {
                    listener.onItemClick(currentItem.url!!,currentItem.name!!,currentItem.duration!!)
                }
            }
            is FrameViewHolder -> {

            }
            is LoadMoreViewHolder -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position % 5 == 0 && position != 0 && position < itemList.size -> ITEM_TYPE_FRAME
            position == itemList.size -> ITEM_TYPE_LOAD_MORE
            else -> ITEM_TYPE_MUSIC
        }
    }
}
