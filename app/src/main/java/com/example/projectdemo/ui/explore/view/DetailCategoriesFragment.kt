package com.example.projectdemo.ui.explore.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.BASE_URL_IMAGE
import com.example.projectdemo.databinding.FragmentDetailCategoriesBinding
import com.example.projectdemo.ui.explore.adapter.DetailCategoriesAdapter
import com.example.projectdemo.ui.explore.viewmodel.ExploreViewModel
import com.example.projectdemo.ui.home.view.HomeFragment
import com.example.projectdemo.ui.home.listener.OnItemClickListener
import com.example.projectdemo.untils.EndlessRecyclerViewScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailCategoriesFragment : Fragment() {
    private lateinit var binding: FragmentDetailCategoriesBinding
    private val viewModel: ExploreViewModel by viewModels()
    private lateinit var adapter: DetailCategoriesAdapter
    var offset = 0
    private lateinit var itemList: MutableList<Any>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailCategoriesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.title = ""
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container_view)
            if (currentFragment != null) {
                fragmentManager.beginTransaction().remove(currentFragment).commit()
            }
        }
        setupViews()
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews() {
        itemList = mutableListOf()
        val arguments = arguments
        val id = arguments?.getInt("id")
        val title = arguments?.getString("title")
        val count = arguments?.getInt("count")
        val url = arguments?.getString("url")
        Glide.with(requireActivity()).load(BASE_URL_IMAGE + url).into(binding.imgCategories)
        binding.imgCategories.setOnClickListener {
        }
        binding.txtTitle.text = title
        binding.txtCount.text = "$count ringtones"

        //recycler view
        binding.recyclerViewDetail.setHasFixedSize(true)
        binding.recyclerViewDetail.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        viewModel.fetchDataDetail(id!!, offset)
        viewModel.dataDetail.observe(requireActivity(), Observer { it ->
            itemList.addAll(it)
            adapter = DetailCategoriesAdapter(itemList)
            adapter.notifyDataSetChanged()
            val layoutManager = binding.recyclerViewDetail.layoutManager as LinearLayoutManager
            val lastVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            if (lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition < itemList.size) {
                layoutManager.scrollToPosition(lastVisiblePosition+1)
            }
            binding.recyclerViewDetail.adapter = adapter
        })

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDetail.layoutManager = layoutManager

        binding.recyclerViewDetail.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                viewModel.fetchHasNext(id, page)
                viewModel.hasNext.observe(requireActivity(), Observer {
                    if (it.hasnext!!) {
                        loadMoreData(id, page)
                    }
                })
            }

        })
    }

    private fun loadMoreData(id: Int, page: Int) {
        viewModel.fetchDataDetail(id, page)
        Toast.makeText(requireContext(), "$page", Toast.LENGTH_SHORT).show()
    }


}