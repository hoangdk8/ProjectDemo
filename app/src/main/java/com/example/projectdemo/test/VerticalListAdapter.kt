//package com.example.project_figma.ui.Home.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ProgressBar
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.project_figma.R
//import com.example.project_figma.data.model.ListHomeModel
//import com.example.project_figma.utils.formatDurationTime
//
//class VerticalListAdapter(
//    private val context: Context,
//    private val itemList: ListHomeModel
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    private var isLoading = false
//
//    companion object {
//        private const val ITEM_TYPE_MUSIC = 0
//        private const val ITEM_TYPE_FRAME = 1
//        private const val ITEM_TYPE_LOADING = 2
//    }
//
//    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val textTitle: TextView = itemView.findViewById(R.id.tvTitle)
//        val textTime: TextView = itemView.findViewById(R.id.tvTime)
//    }
//
//    inner class FrameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//
//    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val loadingBar: ProgressBar = itemView.findViewById(R.id.loadingProgressBar)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            ITEM_TYPE_MUSIC -> {
//                val view =
//                    LayoutInflater.from(context).inflate(R.layout.item_play_music, parent, false)
//                MusicViewHolder(view)
//            }
//
//            ITEM_TYPE_FRAME -> {
//                val view =
//                    LayoutInflater.from(context).inflate(R.layout.item_adversite, parent, false)
//                FrameViewHolder(view)
//            }
//
//            ITEM_TYPE_LOADING -> {
//                val view =
//                    LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false)
//                LoadingViewHolder(view)
//
//            }
//
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (holder) {
//            is MusicViewHolder -> {
//                val currentItem = itemList.items[position]
//                holder.textTitle.text = currentItem.name
//                holder.textTime.text = currentItem.duration?.formatDurationTime() ?: ""
//            }
//
//            is LoadingViewHolder -> {
//                if (isLoading) {
//                    holder.loadingBar.visibility = View.VISIBLE
//                } else {
//                    holder.loadingBar.visibility = View.GONE
//                }
//            }
//
//            is FrameViewHolder -> {}
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.items.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when {
//            position % 3 == 0 && position != 0 && position < itemList.items.size -> ITEM_TYPE_FRAME
//            position == itemList.items.size -> ITEM_TYPE_LOADING
//            else -> ITEM_TYPE_MUSIC
//        }
//    }
//}