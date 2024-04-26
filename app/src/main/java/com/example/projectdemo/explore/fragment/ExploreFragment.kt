package com.example.projectdemo.explore.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentExploreBinding
import com.example.projectdemo.explore.adapter.CategoriesAdapter
import com.example.projectdemo.explore.adapter.TopMusicAdapter
import com.example.projectdemo.explore.listener.OnClickCategoriesListener
import com.example.projectdemo.explore.viewmodel.ExploreViewModel
import com.example.projectdemo.home.fragment.HomeFragment
import com.example.projectdemo.home.interfa.OnItemClickListener
import com.example.projectdemo.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(), OnClickCategoriesListener,OnItemClickListener {
    interface OnDataCategories {
        @SuppressLint("NotConstructor")
        fun onDataCategories(id: Int, title: String, count: Int, url: String)
    }

    private lateinit var adapterCategories: CategoriesAdapter
    private lateinit var adapterTopDown: TopMusicAdapter
    private var dataPassListener: OnDataCategories? = null
    private var dataPass: HomeFragment.OnDataPass? = null
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
        actionView()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragment.OnDataPass) {
            dataPass = context
        } else {
            throw RuntimeException("$context must implement OnDataPass")
        }
    }
    private fun actionView() {
        binding.seeAllTopDown.setOnClickListener {
            val bundle = Bundle().apply {
                putString("HOME_TYPE", "topdown")
                putString("TITLE", "${binding.txtTopDown.text}")
            }
            val fragment = FragmentSeeAll()
            fragment.arguments = bundle
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.anim_left_in,R.anim.anim_left_out)
            fragmentTransaction.replace(R.id.fragment_container_view, fragment)
            fragmentTransaction.commit()
        }
        binding.seeAllTopTrending.setOnClickListener {
            val bundle = Bundle().apply {
                putString("HOME_TYPE", "trends")
                putString("TITLE", "${binding.txtTopTrending.text}")
            }
            val fragment = FragmentSeeAll()
            fragment.arguments = bundle
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.anim_left_in,R.anim.anim_left_out)
            fragmentTransaction.replace(R.id.fragment_container_view, fragment)
            fragmentTransaction.commit()
        }
        binding.seeAllNewRingtone.setOnClickListener {
            val bundle = Bundle().apply {
                putString("HOME_TYPE", "new")
                putString("TITLE", "${binding.txtNewRingtone.text}")
            }
            val fragment = FragmentSeeAll()
            fragment.arguments = bundle
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.anim_left_in,R.anim.anim_left_out)
            fragmentTransaction.replace(R.id.fragment_container_view, fragment)
            fragmentTransaction.commit()
        }
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
            adapterTopDown = TopMusicAdapter(data,this)
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
            adapterTopDown = TopMusicAdapter(data,this)
            binding.recyclerviewTopTrending.adapter = adapterTopDown
        })

        //New Ringtone
        binding.recyclerviewNewRingtone.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewNewRingtone.isFocusable = false
        binding.recyclerviewNewRingtone.isNestedScrollingEnabled = false
        viewModelHome.fetchData(0)
        viewModelHome.data.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "new" }
            adapterTopDown = TopMusicAdapter(data,this)
            binding.recyclerviewNewRingtone.adapter = adapterTopDown
        })
    }

    override fun onItemClick(id: Int, title: String, count: Int, url: String) {
        sendDataToFragment(id, title, count, url)
        Toast.makeText(requireActivity(), "$id", Toast.LENGTH_SHORT).show()
        val bundle = Bundle().apply {
            putInt("id", id)
            putString("title", title)
            putInt("count", count)
            putString("url", url)
        }
        val fragment = DetailCategoriesFragment()
        fragment.arguments = bundle
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.anim_left_in,R.anim.anim_left_out)
        fragmentTransaction.replace(R.id.fragment_container_view, fragment)
        fragmentTransaction.commit()
    }

    private fun sendDataToFragment(id: Int, title: String, count: Int, url: String) {
        dataPassListener?.onDataCategories(id, title, count, url)
    }
    private fun sendDataToActivity(data: String,title: String,time:Int) {
        dataPass?.onDataPass(data,title,time)
    }
    override fun onItemClick(url: String, title: String, time: Int) {
        sendDataToActivity(url,title,time)
    }

}