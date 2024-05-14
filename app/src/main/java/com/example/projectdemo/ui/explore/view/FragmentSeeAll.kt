package com.example.projectdemo.ui.explore.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.untils.EndlessRecyclerViewScrollListener
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentSeeAllBinding
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.ui.explore.adapter.TopMusicAdapter
import com.example.projectdemo.ui.home.view.HomeFragment
import com.example.projectdemo.ui.home.listener.OnItemClickListener
import com.example.projectdemo.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSeeAll : Fragment() {

    private lateinit var binding: FragmentSeeAllBinding
    private lateinit var adapterTopDown: TopMusicAdapter
    private val viewModelHome: HomeViewModel by viewModels()
    private var currentPage = 0
    private lateinit var itemList: MutableList<Any>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViews() {
        itemList = mutableListOf()
        binding.icBack.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container_view)
            if (currentFragment != null) {
                fragmentManager.beginTransaction().remove(currentFragment).commit()
            }
        }
        val arguments = arguments
        val homeType = arguments?.getString("HOME_TYPE")
        val title = arguments?.getString("TITLE")
        binding.txtTitle.text = title
        binding.recyclerViewSeeAllTopDownload.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewSeeAllTopDownload.isFocusable = false
        binding.recyclerViewSeeAllTopDownload.isNestedScrollingEnabled = false
        viewModelHome.getItemsFilter(currentPage)
        viewModelHome.itemListFilter.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == homeType }
            itemList.addAll(data)
            Log.d("hoang", "setupViews:$currentPage ")
            adapterTopDown = TopMusicAdapter(itemList as List<DataDefaultRings.RingTone>)
            adapterTopDown.notifyDataSetChanged()
            val layoutManager = binding.recyclerViewSeeAllTopDownload.layoutManager as LinearLayoutManager
            val lastVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            if (lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition < itemList.size) {
                layoutManager.scrollToPosition(lastVisiblePosition)
            }
            binding.recyclerViewSeeAllTopDownload.adapter = adapterTopDown
        })
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSeeAllTopDownload.layoutManager = layoutManager

        binding.recyclerViewSeeAllTopDownload.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadMoreData(page)
            }

        })
            
    }

    private fun loadMoreData(page: Int) {
        viewModelHome.getItemsFilter(page)
    }


}