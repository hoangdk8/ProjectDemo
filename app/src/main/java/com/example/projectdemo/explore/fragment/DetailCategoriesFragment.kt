package com.example.projectdemo.explore.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentDetailCategoriesBinding
import com.example.projectdemo.databinding.FragmentHomeBinding

class DetailCategoriesFragment : Fragment(),ExploreFragment.OnDataCategories {
    private lateinit var binding: FragmentDetailCategoriesBinding
    val baseImageUrl =
        "https://pub-a59f0b5c0b134cdb808fe708183c7d0e.r2.dev/ringstorage/categories/fr2019tkv2/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("SetTextI18n")
    private fun setupViews(id: Int, title: String, count: Int, url: String) {
        Glide.with(requireActivity()).load(baseImageUrl + url).into(binding.imgCategories)
        binding.imgCategories.setOnClickListener {
            Log.d("hoang", "setupViews: ")
        }
        binding.txtTitle.text = title
        binding.txtCount.text = "$count ringtones"
        binding.imgBack.setOnClickListener {
            Log.d("hoang", "setupViews:aaaaaaaa ")
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = DetailCategoriesFragment()
            fragmentTransaction.remove(newFragment)
            //fragmentTransaction.hide(newFragment)// R.id.fragment_container là ID của container trong layout của Activity chứa Fragment hiện tại
            fragmentTransaction.commit()
        }
    }

    override fun onDataCategories(id: Int, title: String, count: Int, url: String) {
        setupViews(id, title, count, url)
    }
}