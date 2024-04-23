package com.example.projectdemo.home.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentHomeBinding
import com.example.projectdemo.dataclass.MusicBanner
import com.example.projectdemo.home.adapter.AdapterMusicTop
import com.example.projectdemo.home.adapter.HomeAdapter
import com.example.projectdemo.home.interfa.OnItemClickListener
import com.example.projectdemo.home.viewmodel.HomeViewModel
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {
    interface OnDataPass {
        fun onDataPass(data: String,title: String,time:Int)
    }

    private lateinit var musicTopList: ArrayList<MusicBanner>
    private lateinit var adapterMusicTop: AdapterMusicTop
    private lateinit var adapter: HomeAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var exoPlayer: ExoPlayer
    private var currentPage = 0
    private var dataPassListener: OnDataPass? = null
    private lateinit var itemList: MutableList<Any>
    private var isUserScrolling = false


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

    private fun sendDataToActivity(data: String,title: String,time:Int) {
        dataPassListener?.onDataPass(data,title,time)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViews() {
        //Banner
        binding.bannerRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.bannerRecyclerView.isFocusable = false
        binding.bannerRecyclerView.isNestedScrollingEnabled = false
        musicTopList = ArrayList()
        addMusic()
        adapterMusicTop = AdapterMusicTop(musicTopList)
        binding.bannerRecyclerView.adapter = adapterMusicTop
        //Playlist
        itemList = mutableListOf()
        binding.mainRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.mainRecyclerView.isFocusable = false
        binding.mainRecyclerView.isNestedScrollingEnabled = false
        viewModel.fetchData(currentPage)
        viewModel.data.observe(requireActivity(), Observer { data ->
            itemList.addAll(data)
            adapter.notifyDataSetChanged()
        })
        adapter = HomeAdapter(itemList, this)
        binding.mainRecyclerView.adapter = adapter
        if (binding.mainRecyclerView.childCount == 0) {
            binding.loading.visibility = View.VISIBLE
            binding.bannerRecyclerView.visibility = View.GONE
        }
        binding.mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.childCount >0){
                    binding.loading.visibility = View.GONE
                    binding.bannerRecyclerView.visibility = View.VISIBLE
                }
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItemPosition== totalItemCount-1){
                    currentPage++
                    //loadMore()
                }
                Log.d("hoang", "onScrolled: $lastVisibleItemPosition -- $totalItemCount")
            }
        })

    }

    private fun loadMore() {
        if (currentPage==1){
            viewModel.fetchData(currentPage)
            viewModel.data.observe(requireActivity(), Observer { data ->
                itemList.addAll(data)
                adapter = HomeAdapter(itemList, this)
                Log.d("hoang", "loadMore: $currentPage")
                binding.mainRecyclerView.adapter = adapter
                Log.d("hoang", "loadMore:${adapter.itemCount} ")
            })
        }

    }

    private fun addMusic() {
        musicTopList.add(MusicBanner("[KBS] 오아시스 OST", R.drawable.image1))
        musicTopList.add(MusicBanner("[SBS] 낭만닥터 김사부 OST", R.drawable.image2))
        musicTopList.add(MusicBanner("더 글로리 OST", R.drawable.image3))
    }

    override fun onItemClick(url: String,title: String,time:Int) {
        sendDataToActivity(url,title,time)
    }

}
