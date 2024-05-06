package com.example.projectdemo.me

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentFavoritesBinding
import com.example.projectdemo.databinding.FragmentSeeAllBinding
import com.example.projectdemo.explore.adapter.TopMusicAdapter
import com.example.projectdemo.home.viewmodel.HomeViewModel
import com.example.projectdemo.me.adapter.FavoritesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesAdapter
    private val viewModelHome: HomeViewModel by viewModels()
    private var currentPage = 0
    private lateinit var itemList: MutableList<Any>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        itemList = mutableListOf()
        binding.recyclerViewFavorites.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        viewModelHome.getItemsFilter(currentPage)
        viewModelHome.itemListFilter.observe(requireActivity(), Observer { it ->

            itemList.addAll(it)
            Log.d("hoang", "setupViews:$currentPage ")
            adapter = FavoritesAdapter(itemList as List<DataDefaultRings.Data>)
            if (adapter.itemCount > 1) {
                binding.loading.visibility = View.GONE
            }
//            adapter.notifyDataSetChanged()
//            val layoutManager = binding.recyclerViewFavorites.layoutManager as LinearLayoutManager
//            val lastVisiblePosition = layoutManager.findFirstVisibleItemPosition()
//            if (lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition < itemList.size) {
//                layoutManager.scrollToPosition(lastVisiblePosition)
//            }
            binding.recyclerViewFavorites.adapter = adapter
        })
    }
}