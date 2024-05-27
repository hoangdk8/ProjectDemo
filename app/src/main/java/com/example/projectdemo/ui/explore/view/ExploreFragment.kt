package com.example.projectdemo.ui.explore.view

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
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentExploreBinding
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.eventbus.EventRefreshExplore
import com.example.projectdemo.ui.detailplaymusic.DetailPlayMusicFragment
import com.example.projectdemo.ui.explore.adapter.CategoriesAdapter
import com.example.projectdemo.ui.explore.adapter.TopMusicAdapter
import com.example.projectdemo.ui.explore.listener.OnClickCategoriesListener
import com.example.projectdemo.ui.explore.viewmodel.ExploreViewModel
import com.example.projectdemo.ui.home.viewmodel.HomeViewModel
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : Fragment(), OnClickCategoriesListener,DetailPlayMusic {
    interface OnDataCategories {
        @SuppressLint("NotConstructor")
        fun onDataCategories(id: Int, title: String, count: Int, url: String)
    }
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var adapterCategories: CategoriesAdapter
    private lateinit var adapterTopDown: TopMusicAdapter
    private var dataPassListener: OnDataCategories? = null
    private lateinit var binding: FragmentExploreBinding
    private val viewModel: ExploreViewModel by viewModels()
    private val viewModelHome: HomeViewModel by viewModels()
    private lateinit var exoPlayer: ExoPlayer
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
        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
    }
    private fun actionView() {
        binding.seeAllTopDown.setOnClickListener {
            val bundle = Bundle().apply {
                putString("HOME_TYPE", "topdown")
                putString("TITLE", "${binding.txtTopDown.text}")
            }
            val fragment = FragmentSeeAll()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.anim_left_in,
                    R.anim.anim_left_out,
                    R.anim.anim_left_in,
                    R.anim.anim_left_out
                )
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.seeAllTopTrending.setOnClickListener {
            val bundle = Bundle().apply {
                putString("HOME_TYPE", "trends")
                putString("TITLE", "${binding.txtTopTrending.text}")
            }
            val fragment = FragmentSeeAll()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.anim_left_in,
                    R.anim.anim_left_out,
                    R.anim.anim_left_in,
                    R.anim.anim_left_out
                )
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.seeAllNewRingtone.setOnClickListener {
            val bundle = Bundle().apply {
                putString("HOME_TYPE", "new")
                putString("TITLE", "${binding.txtNewRingtone.text}")
            }
            val fragment = FragmentSeeAll()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.anim_left_in,
                    R.anim.anim_left_out,
                    R.anim.anim_left_in,
                    R.anim.anim_left_out
                )
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
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
            adapterCategories = CategoriesAdapter(requireActivity(), data!!, this)
            binding.recyclerviewCategories.adapter = adapterCategories
        })

        //Top Download
        binding.recyclerviewTopDown.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewTopDown.isFocusable = false
        binding.recyclerviewTopDown.isNestedScrollingEnabled = false
        viewModelHome.getItemsFilter(0)
        viewModelHome.itemListFilter.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "topdown" }
            adapterTopDown = TopMusicAdapter(data,exoPlayerManager,this)
            binding.recyclerviewTopDown.adapter = adapterTopDown
        })

        //Top Trending
        binding.recyclerviewTopTrending.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewTopTrending.isFocusable = false
        binding.recyclerviewTopTrending.isNestedScrollingEnabled = false
        viewModelHome.getItemsFilter(0)
        viewModelHome.itemListFilter.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "trends" }
            adapterTopDown = TopMusicAdapter(data,exoPlayerManager,this)
            binding.recyclerviewTopTrending.adapter = adapterTopDown
        })

        //New Ringtone
        binding.recyclerviewNewRingtone.layoutManager =
            GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerviewNewRingtone.isFocusable = false
        binding.recyclerviewNewRingtone.isNestedScrollingEnabled = false
        viewModelHome.getItemsFilter(0)
        viewModelHome.itemListFilter.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "new" }
            adapterTopDown = TopMusicAdapter(data,exoPlayerManager,this)
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
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.anim_left_in,
                R.anim.anim_left_out,
                R.anim.anim_left_in,
                R.anim.anim_left_out
            )
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }
    @Subscribe
    fun onEvent(event: EventRefreshExplore) {
//        val layoutManager = binding.mainRecyclerView.layoutManager as LinearLayoutManager
//            layoutManager.scrollToPosition(0)
    }
    private fun sendDataToFragment(id: Int, title: String, count: Int, url: String) {
        dataPassListener?.onDataCategories(id, title, count, url)
    }
    override fun onDestroy() {
        super.onDestroy()
        eventBusUnRegister()
    }

    override fun onShowDetailsMusic(ringTone: DataDefaultRings.RingTone) {
        val bundle = Bundle().apply {
            putInt("id", ringTone.id!!)
            putString("name", ringTone.name)
            putString("categories", ringTone.categories)
            putInt("duration", ringTone.duration!!)
            putInt("count", ringTone.count!!)
            putString("url", ringTone.url)
            putString("hometype", ringTone.hometype)
            putInt("isVip", ringTone.isVip!!)
            putString("datatype", ringTone.datatype)
            putBoolean("online", ringTone.online == true)
        }
        val fragment = DetailPlayMusicFragment()
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.anim_left_in,
                R.anim.anim_left_out,
                R.anim.anim_left_in,
                R.anim.anim_left_out
            )
            .replace(R.id.fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}