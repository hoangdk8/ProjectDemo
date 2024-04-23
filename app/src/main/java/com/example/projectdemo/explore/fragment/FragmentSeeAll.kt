package com.example.projectdemo.explore.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.databinding.FragmentSeeAllBinding
import com.example.projectdemo.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSeeAll : Fragment() {

    private var _binding: FragmentSeeAllBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelHomeType: HomeViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeeAllBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModelHomeType = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerView()
        observeData()

        return view
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSeeAllTopDownload.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSeeAllTopDownload.setHasFixedSize(true)
    }

    private fun observeData() {
        viewModelHomeType.fetchData(0)
//        viewModelHomeType.itemList.observe(viewLifecycleOwner) { dataList ->
//            val adapter = when {
//                arguments?.getString("homeType") == "topdown" -> {
//                    val topDownloadList = dataList.items.filter { it.homeType == "topdown" }
//                    TopDownAdapter(topDownloadList)
//                }
//                arguments?.getString("homeType") == "trends" -> {
//                    val trendingList = dataList.items.filter { it.homeType == "trends" }
//                    TopTrendingAdapter(trendingList)
//                }
//                arguments?.getString("homeType") == "new" -> {
//                    val newList = dataList.items.filter { it.homeType == "new" }
//                    NewAdapter(newList)
//                }
//                else -> {
//                    TopDownAdapter(emptyList())
//                }
//            }
//            binding.recyclerViewSeeAllTopDownload.adapter = adapter
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}