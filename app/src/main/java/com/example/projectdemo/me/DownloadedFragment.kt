package com.example.projectdemo.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentDownloadedBinding
import com.example.projectdemo.home.viewmodel.HomeViewModel
import com.example.projectdemo.me.adapter.DownloadAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadedFragment : Fragment() {
    private lateinit var binding: FragmentDownloadedBinding
    private lateinit var adapter: DownloadAdapter
    private val viewModelHome: HomeViewModel by viewModels()
    private var currentPage = 0
    private lateinit var itemList: MutableList<Any>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        itemList = mutableListOf()
        binding.recyclerViewDownload.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        viewModelHome.getItemsFilter(currentPage)
        viewModelHome.itemListFilter.observe(requireActivity(), Observer { it ->

            itemList.addAll(it)
            adapter = DownloadAdapter(itemList as List<DataDefaultRings.Data>)
            if (adapter.itemCount > 1) {
                binding.loading.visibility = View.GONE
            }
            binding.recyclerViewDownload.adapter = adapter
//            if (adapter.itemCount > 0) {
//                binding.loading.visibility = View.GONE
//            }
        })
    }
}