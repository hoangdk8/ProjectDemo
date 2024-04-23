package com.example.projectdemo.explore.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentDetailCategoriesBinding
import com.example.projectdemo.explore.adapter.DetailCategoriesAdapter
import com.example.projectdemo.explore.viewmodel.DetailCategoriesViewModel
import com.example.projectdemo.home.adapter.HomeAdapter
import com.example.projectdemo.home.fragment.HomeFragment
import com.example.projectdemo.home.interfa.OnItemClickListener
import com.example.projectdemo.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailCategoriesFragment : Fragment(), ExploreFragment.OnDataCategories, OnItemClickListener {
    private lateinit var binding: FragmentDetailCategoriesBinding
    private val viewModel: DetailCategoriesViewModel by viewModels()
    private lateinit var adapter: DetailCategoriesAdapter
    private var dataPassListener: HomeFragment.OnDataPass? = null
    val baseImageUrl =
        "https://pub-a59f0b5c0b134cdb808fe708183c7d0e.r2.dev/ringstorage/categories/fr2019tkv2/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailCategoriesBinding.inflate(inflater, container, false)
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
        val arguments = arguments
        val id = arguments?.getInt("id")
        val title = arguments?.getString("title")
        val count = arguments?.getInt("count")
        val url = arguments?.getString("url")
        Log.d("hoang", "setupViews:$id,$title,$count,$url ")
        Glide.with(requireActivity()).load(baseImageUrl + url).into(binding.imgCategories)
        binding.imgCategories.setOnClickListener {
            Log.d("hoang", "setupViews: ")
        }
        binding.txtTitle.text = title
        binding.txtCount.text = "$count ringtones"

        //recycler view
        binding.recyclerViewDetail.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewDetail.isFocusable = false
        binding.recyclerViewDetail.isNestedScrollingEnabled = false
        viewModel.fetchData(id!!)
        viewModel.data.observe(requireActivity(), Observer { it ->

            adapter = DetailCategoriesAdapter(it,this)
            binding.recyclerViewDetail.adapter = adapter
            Log.d("hoang", "setupViewss: ${adapter.itemCount}")
        })
    }

    override fun onDataCategories(id: Int, title: String, count: Int, url: String) {
    }

    override fun onItemClick(url: String, title: String, time: Int) {
        sendDataToActivity(url,title,time)
    }
}