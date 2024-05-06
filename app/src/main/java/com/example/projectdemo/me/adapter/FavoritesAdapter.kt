package com.example.projectdemo.me.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.ItemFavoritesBinding
import com.example.projectdemo.home.listener.OnItemClickListener
import com.example.projectdemo.untils.convertDurationToTimeString

class FavoritesAdapter (private val favorites :List<DataDefaultRings.Data>
) :
RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var selectedPosition = RecyclerView.NO_POSITION
    private val heartStates = Array<Boolean>(itemCount) { false }
    inner class FavoritesViewHolder(private val binding: ItemFavoritesBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindFavorites(data : DataDefaultRings.Data, position: Int){
            val result = convertDurationToTimeString(data.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime.text = "$hour:$minute"
            binding.txtTitle.text = data.name
            binding.imgStatus.setImageResource(R.drawable.ic_play)
            binding.imgStatus.setOnClickListener {
//                listener.onItemClick(
//                    data.url!!,
//                    data.name!!,
//                    data.duration!!
//                )
                selectedPosition = position
            }
            binding.imgHeart.setImageResource(if (heartStates[position]) R.drawable.ic_heart_black else R.drawable.ic_heart)
            binding.imgHeart.setOnClickListener {
                heartStates[position] = !heartStates[position]
                binding.imgHeart.setImageResource(if (heartStates[position]) R.drawable.ic_heart_black else R.drawable.ic_heart)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = ItemFavoritesBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return FavoritesViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return favorites.size
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bindFavorites(favorites[position],position)
    }
}