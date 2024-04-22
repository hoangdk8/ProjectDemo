package com.example.projectdemo.explore.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.databinding.FragmentExploreBinding
import com.example.projectdemo.explore.adapter.CategoriesAdapter
import com.example.projectdemo.explore.adapter.NewRingtoneAdapter
import com.example.projectdemo.explore.adapter.TopDownAdapter
import com.example.projectdemo.explore.adapter.TopTrendingAdapter
import com.example.projectdemo.explore.listener.OnClickCategoriesListener
import com.example.projectdemo.explore.viewmodel.ExploreViewModel
import com.example.projectdemo.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(), OnClickCategoriesListener {
    interface OnDataCategories {
        @SuppressLint("NotConstructor")
        fun onDataCategories(id: Int, title: String, count: Int, url: String)
    }

    private lateinit var adapterCategories: CategoriesAdapter
    private lateinit var adapterTopDown: TopDownAdapter
    private lateinit var adapterTopTrending: TopTrendingAdapter
    private lateinit var adapterNewRingtone: NewRingtoneAdapter
    private var dataPassListener: OnDataCategories? = null
    private lateinit var binding: FragmentExploreBinding
    private val viewModel: ExploreViewModel by viewModels()
    private val viewModelHome: HomeViewModel by viewModels()
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
            adapterCategories = CategoriesAdapter(requireActivity(), data, this)
            binding.recyclerviewCategories.adapter = adapterCategories
        })

        //Top Download
        binding.recyclerviewTopDown.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewTopDown.isFocusable = false
        binding.recyclerviewTopDown.isNestedScrollingEnabled = false
        viewModelHome.fetchData(0)
        viewModelHome.data.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "topdown" }
            adapterTopDown = TopDownAdapter(data)
            binding.recyclerviewTopDown.adapter = adapterTopDown
        })

        //Top Trending
        binding.recyclerviewTopTrending.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewTopTrending.isFocusable = false
        binding.recyclerviewTopTrending.isNestedScrollingEnabled = false
        viewModelHome.fetchData(0)
        viewModelHome.data.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "trends" }
            adapterTopTrending = TopTrendingAdapter(data)
            binding.recyclerviewTopTrending.adapter = adapterTopTrending
        })

        //New Ringtone
        binding.recyclerviewNewRingtone.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewNewRingtone.isFocusable = false
        binding.recyclerviewNewRingtone.isNestedScrollingEnabled = false
        viewModelHome.fetchData(0)
        viewModelHome.data.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "new" }
            adapterNewRingtone = NewRingtoneAdapter(data)
            binding.recyclerviewNewRingtone.adapter = adapterNewRingtone
        })
    }

    override fun onItemClick(id: Int, title: String, count: Int, url: String) {
        sendDataToFragment(id, title, count, url)
//        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        val newFragment = DetailCategoriesFragment()
//        fragmentTransaction.replace(R.id.fragment_container_view, newFragment) // R.id.fragment_container là ID của container trong layout của Activity chứa Fragment hiện tại
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
    }

    private fun sendDataToFragment(id: Int, title: String, count: Int, url: String) {
        dataPassListener?.onDataCategories(id, title, count, url)
    }
}