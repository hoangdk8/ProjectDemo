package com.example.projectdemo.ui.explore.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.projectdemo.data.dataclass.DataDefaultRings

class DiffUtilList(
    private val oldList: List<DataDefaultRings.RingTone>,
    private val newList: List<DataDefaultRings.RingTone>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition].id != newList[newItemPosition].id -> {
                false
            }
            oldList[oldItemPosition].name != newList[newItemPosition].name -> {
                false
            }
            oldList[oldItemPosition].duration != newList[newItemPosition].duration -> {
                false
            }
            oldList[oldItemPosition].url != newList[newItemPosition].url -> {
                false
            }
            else -> true
        }
    }
}