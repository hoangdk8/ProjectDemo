package com.example.projectdemo.explore.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentDetailCategoriesBinding
import com.example.projectdemo.databinding.FragmentSeeAllBinding
import com.example.projectdemo.explore.adapter.CategoriesAdapter
import com.example.projectdemo.explore.adapter.TopMusicAdapter
import com.example.projectdemo.home.fragment.HomeFragment
import com.example.projectdemo.home.interfa.OnItemClickListener
import com.example.projectdemo.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSeeAll : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentSeeAllBinding
    private lateinit var adapterTopDown: TopMusicAdapter
    private val viewModelHome: HomeViewModel by viewModels()
    private var dataPassListener: HomeFragment.OnDataPass? = null
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragment.OnDataPass) {
            dataPassListener = context
        } else {
            throw RuntimeException("$context must implement OnDataPass")
        }
    }
    private fun sendDataToActivity(data: String,title: String,time:Int) {
        dataPassListener?.onDataPass(data,title,time)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
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
        viewModelHome.fetchData(currentPage)
        viewModelHome.data.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == homeType }
            adapterTopDown = TopMusicAdapter(data,this)
            binding.recyclerViewSeeAllTopDownload.adapter = adapterTopDown
        })

        binding.recyclerViewSeeAllTopDownload.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            }
        })
    }

    override fun onItemClick(url: String, title: String, time: Int) {
        sendDataToActivity(url,title,time)
    }

}