package com.example.projectdemo.home.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.databinding.FragmentHomeBinding
import com.example.projectdemo.explore.adapter.TopMusicAdapter
import com.example.projectdemo.home.adapter.HomeAdapter
import com.example.projectdemo.home.listener.OnItemClickListener
import com.example.projectdemo.home.viewmodel.HomeViewModel
import com.example.projectdemo.untils.EndlessRecyclerViewScrollListener
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {
    interface OnDataPass {
        fun onDataPass(data: String, title: String, time: Int)
    }
    private lateinit var adapter: HomeAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var exoPlayer: ExoPlayer
    private var dataPassListener: OnDataPass? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDataPass) {
            dataPassListener = context
        } else {
            throw RuntimeException("$context must implement OnDataPass")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
    }

    private fun sendDataToActivity(data: String, title: String, time: Int) {
        dataPassListener?.onDataPass(data, title, time)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViews() {
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.itemList.observe(requireActivity(), Observer { it ->
            adapter = HomeAdapter(it)

            adapter.notifyDataSetChanged()
            val layoutManager = binding.mainRecyclerView.layoutManager as LinearLayoutManager
            val lastVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            if (lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition < it.size) {
                layoutManager.scrollToPosition(lastVisiblePosition)
            }
            binding.mainRecyclerView.adapter = adapter
            if (adapter.itemCount > 1) {
                binding.loading.visibility = View.GONE
            }

            adapter.onClickItemListener {
                sendDataToActivity(it.url ?: "", it.name ?: "", it.duration ?: 0)
            }
        })
        val layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerView.layoutManager = layoutManager
        binding.mainRecyclerView.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (totalItemsCount>1 && page<4) {
                    loadMoreData(page-1)
                }
            }

        })
    }

    private fun loadMoreData(page: Int) {
        viewModel.getItems(page)
        Toast.makeText(requireActivity(), "$page", Toast.LENGTH_SHORT).show()
    }


    override fun onItemClick(url: String, title: String, time: Int) {
        sendDataToActivity(url, title, time)
    }

}
