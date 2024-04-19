//package com.example.project_figma.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.project_figma.databinding.FragmentHomeBinding
//import com.example.project_figma.ui.Home.HomeViewModel
//import com.example.project_figma.ui.Home.adapter.HorizontalListAdapter
//import com.example.project_figma.ui.Home.adapter.VerticalListAdapter
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class HomeScreen : Fragment() {
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var viewModel: HomeViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
//        val horizontalAdapter =
//            HorizontalListAdapter(requireContext(), viewModel.getItemListHorizontal())
//        binding.loadingProgressBar.visibility = View.VISIBLE
//        binding.horizontalRecyclerView.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        binding.horizontalRecyclerView.adapter = horizontalAdapter
//
//        binding.verticalRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val totalItemCount = layoutManager.itemCount
//                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//                if (totalItemCount <= lastVisibleItem + 2) {
//                    viewModel.loadMoreItems()
//                }
//            }
//        })
//
//        viewModel.itemList.observe(viewLifecycleOwner) { itemList ->
//            val verticalAdapter = VerticalListAdapter(requireContext(), itemList)
//            binding.verticalRecyclerView.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//          binding.verticalRecyclerView.canScrollVertically(1)
//            binding.verticalRecyclerView.adapter = verticalAdapter
//            binding.loadingProgressBar.visibility = View.GONE
//        }
//
//        return view
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}