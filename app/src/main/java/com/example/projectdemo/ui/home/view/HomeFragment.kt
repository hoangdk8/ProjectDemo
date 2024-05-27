package com.example.projectdemo.ui.home.view

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentHomeBinding
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.eventbus.EventGoneView
import com.example.projectdemo.listener.eventbus.EventRefreshHome
import com.example.projectdemo.ui.detailplaymusic.DetailPlayMusicFragment
import com.example.projectdemo.ui.home.adapter.HomeAdapter
import com.example.projectdemo.ui.home.viewmodel.HomeViewModel
import com.example.projectdemo.untils.EndlessRecyclerViewScrollListener
import com.example.projectdemo.untils.eventBusPost
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(), DetailPlayMusic {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var adapter: HomeAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var exoPlayer: ExoPlayer


    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViews() {
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.itemList.observe(requireActivity(), Observer { it ->
            adapter = HomeAdapter(requireContext(),it, exoPlayerManager, this)
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

        })
        val layoutManager = LinearLayoutManager(requireContext())
        binding.mainRecyclerView.layoutManager = layoutManager
        binding.mainRecyclerView.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (totalItemsCount > 1 && page < 4) {
                    loadMoreData(page - 1)
                }
            }

        })
    }


    @Subscribe
    fun onEvent(event: EventRefreshHome) {
//        val layoutManager = binding.mainRecyclerView.layoutManager as LinearLayoutManager
//            layoutManager.scrollToPosition(0)
    }

    private fun loadMoreData(page: Int) {
        viewModel.getItems(page)
        Toast.makeText(requireActivity(), "$page", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBusUnRegister()
    }

    override fun onShowDetailsMusic(ringTone: DataDefaultRings.RingTone) {
        eventBusPost(EventGoneView())
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

//        val layoutManager = binding.mainRecyclerView.layoutManager as LinearLayoutManager
//            layoutManager.scrollToPosition(0)
    }
}
