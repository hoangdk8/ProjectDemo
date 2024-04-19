package com.example.projectdemo.explore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentExploreBinding
import com.example.projectdemo.dataclass.Categories
import com.example.projectdemo.dataclass.NewRingtone
import com.example.projectdemo.dataclass.TopDown
import com.example.projectdemo.dataclass.TopTrending
import com.example.projectdemo.explore.adapter.ExploreAdapter
import com.example.projectdemo.explore.adapter.NewRingtoneAdapter
import com.example.projectdemo.explore.adapter.TopDownAdapter
import com.example.projectdemo.explore.adapter.TopTrendingAdapter
import com.example.projectdemo.explore.viewmodel.ExploreViewModel
import com.example.projectdemo.home.adapter.HomeAdapter
import com.example.projectdemo.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment() {
    private lateinit var adapterCategories: ExploreAdapter
    private lateinit var adapterTopDown: TopDownAdapter
    private lateinit var adapterTopTrending: TopTrendingAdapter
    private lateinit var adapterNewRingtone: NewRingtoneAdapter
    private lateinit var binding: FragmentExploreBinding
    private val viewModel: ExploreViewModel by viewModels()
    private val viewModelHome : HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        //categories
        binding.recyclerviewCategories.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewCategories.isFocusable = false
        binding.recyclerviewCategories.isNestedScrollingEnabled = false
        viewModel.fetchData()
        viewModel.data.observe(requireActivity(), Observer { data ->
            adapterCategories = ExploreAdapter(requireActivity(),data)
            binding.recyclerviewCategories.adapter = adapterCategories
        })

        //Top Download
        binding.recyclerviewTopDown.layoutManager = GridLayoutManager(requireActivity(),3,GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewTopDown.isFocusable = false
        binding.recyclerviewTopDown.isNestedScrollingEnabled = false
        viewModelHome.fetchData(0)
        viewModelHome.data.observe(requireActivity(), Observer{it ->
            val data = it.filter { it.hometype == "topdown" }
            adapterTopDown = TopDownAdapter(data)
            binding.recyclerviewTopDown.adapter = adapterTopDown
        })

        //Top Trending
        binding.recyclerviewTopTrending.layoutManager = GridLayoutManager(requireActivity(),3,GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewTopTrending.isFocusable = false
        binding.recyclerviewTopTrending.isNestedScrollingEnabled = false
        viewModelHome.fetchData(0)
        viewModelHome.data.observe(requireActivity(), Observer{it ->
            val data = it.filter { it.hometype == "trends" }
            adapterTopTrending = TopTrendingAdapter(data)
            binding.recyclerviewTopTrending.adapter = adapterTopTrending
        })

        //New Ringtone
        binding.recyclerviewNewRingtone.layoutManager = GridLayoutManager(requireActivity(),3,GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewNewRingtone.isFocusable = false
        binding.recyclerviewNewRingtone.isNestedScrollingEnabled = false
        viewModelHome.fetchData(0)
        viewModelHome.data.observe(requireActivity(), Observer{it ->
            val data = it.filter { it.hometype == "new" }
            adapterNewRingtone = NewRingtoneAdapter(data)
            binding.recyclerviewNewRingtone.adapter = adapterNewRingtone
        })
    }
}