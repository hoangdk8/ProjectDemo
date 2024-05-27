package com.example.projectdemo.ui.explore.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.untils.EndlessRecyclerViewScrollListener
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentSeeAllBinding
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.eventbus.EventRefreshSeeAll
import com.example.projectdemo.ui.detailplaymusic.DetailPlayMusicFragment
import com.example.projectdemo.ui.explore.adapter.TopMusicAdapter
import com.example.projectdemo.ui.home.viewmodel.HomeViewModel
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@AndroidEntryPoint
class FragmentSeeAll : Fragment(),DetailPlayMusic {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var binding: FragmentSeeAllBinding
    private lateinit var adapterTopDown: TopMusicAdapter
    private val viewModelHome: HomeViewModel by viewModels()
    private var currentPage = 0
    private lateinit var itemList: MutableList<Any>
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViews() {
        itemList = mutableListOf()
        binding.icBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val arguments = arguments
        val homeType = arguments?.getString("HOME_TYPE")
        val title = arguments?.getString("TITLE")
        binding.txtTitle.text = title
        binding.recyclerViewSeeAllTopDownload.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewSeeAllTopDownload.isFocusable = false
        binding.recyclerViewSeeAllTopDownload.isNestedScrollingEnabled = false
        viewModelHome.getItemsFilter(currentPage)
        viewModelHome.itemListFilter.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == homeType }
            itemList.addAll(data)
            Log.d("hoang", "setupViews:$currentPage ")
            adapterTopDown = TopMusicAdapter(itemList as List<DataDefaultRings.RingTone>,exoPlayerManager,this)
            adapterTopDown.notifyDataSetChanged()
            val layoutManager = binding.recyclerViewSeeAllTopDownload.layoutManager as LinearLayoutManager
            val lastVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            if (lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition < itemList.size) {
                layoutManager.scrollToPosition(lastVisiblePosition)
            }
            binding.recyclerViewSeeAllTopDownload.adapter = adapterTopDown
        })
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSeeAllTopDownload.layoutManager = layoutManager

        binding.recyclerViewSeeAllTopDownload.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadMoreData(page)
            }

        })
            
    }
    @Subscribe
    fun onEvent(event: EventRefreshSeeAll) {
//        val layoutManager = binding.mainRecyclerView.layoutManager as LinearLayoutManager
//            layoutManager.scrollToPosition(0)
    }
    private fun loadMoreData(page: Int) {
        viewModelHome.getItemsFilter(page)
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
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}