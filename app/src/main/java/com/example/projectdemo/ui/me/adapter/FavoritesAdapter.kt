package com.example.projectdemo.ui.me.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.ItemFavoritesBinding
import com.example.projectdemo.event.EventUnFavorite
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusPost

class FavoritesAdapter(
    private val favorites: List<DataDefaultRings.RingTone>
) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION


    inner class FavoritesViewHolder(private val binding: ItemFavoritesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bindFavorites(ringTone: DataDefaultRings.RingTone, position: Int) {
            val result = convertDurationToTimeString(ringTone.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime.text = "$hour:$minute"
            binding.txtTitle.text = ringTone.name
            binding.imgStatus.setImageResource(R.drawable.ic_play)
            binding.imgStatus.setOnClickListener {
//                listener.onItemClick(
//                    data.url!!,
//                    data.name!!,
//                    data.duration!!
//                )
                selectedPosition = position
            }
            binding.imgHeart.setImageResource(R.drawable.ic_heart_black)
            binding.imgHeart.setOnClickListener {
                eventBusPost(EventUnFavorite(ringTone.id!!))
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ItemFavoritesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoritesViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return favorites.size
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bindFavorites(favorites[position], position)
    }
}