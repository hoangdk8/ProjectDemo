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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.BASE_URL_IMAGE
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentDetailCategoriesBinding
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.eventbus.EventRefreshSeeAll
import com.example.projectdemo.ui.detailplaymusic.DetailPlayMusicFragment
import com.example.projectdemo.ui.explore.adapter.DetailCategoriesAdapter
import com.example.projectdemo.ui.explore.viewmodel.ExploreViewModel
import com.example.projectdemo.untils.EndlessRecyclerViewScrollListener
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@AndroidEntryPoint
class DetailCategoriesFragment : Fragment(),DetailPlayMusic {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var binding: FragmentDetailCategoriesBinding
    private val viewModel: ExploreViewModel by viewModels()
    private lateinit var adapter: DetailCategoriesAdapter
    var offset = 0
    private lateinit var itemList: MutableList<Any>
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailCategoriesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
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
        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
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
            adapter = DetailCategoriesAdapter(itemList,exoPlayerManager,this)
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
    @Subscribe
    fun onEvent(event: EventRefreshSeeAll) {
//        val layoutManager = binding.mainRecyclerView.layoutManager as LinearLayoutManager
//            layoutManager.scrollToPosition(0)
    }
    private fun loadMoreData(id: Int, page: Int) {
        viewModel.fetchDataDetail(id, page)
        Toast.makeText(requireContext(), "$page", Toast.LENGTH_SHORT).show()
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