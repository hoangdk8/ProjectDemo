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
import com.example.projectdemo.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.databinding.FragmentHomeBinding
import com.example.projectdemo.ui.detailplaymusic.DetailPlayMusicFragment
import com.example.projectdemo.event.EventPlayDetailMusic
import com.example.projectdemo.event.EventRefreshHome
import com.example.projectdemo.ui.home.adapter.HomeAdapter
import com.example.projectdemo.ui.home.listener.OnItemClickListener
import com.example.projectdemo.ui.home.viewmodel.HomeViewModel
import com.example.projectdemo.untils.EndlessRecyclerViewScrollListener
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {
    interface OnDataPass {
        fun onDataPass(data: String, title: String, time: Int)
    }
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var adapter: HomeAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var exoPlayer: ExoPlayer
    private var dataPassListener: OnDataPass? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
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
            adapter = HomeAdapter(it,exoPlayerManager)

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
                if (totalItemsCount > 1 && page < 4) {
                    loadMoreData(page - 1)
                }
            }

        })
    }
    @Subscribe
    fun eventDetailPlay(event: EventPlayDetailMusic) {
        val bundle = Bundle().apply {
            putInt("id", event.ringTone.id!!)
            putString("name", event.ringTone.name)
            putString("categories", event.ringTone.categories)
            putInt("duration", event.ringTone.duration!!)
            putInt("count", event.ringTone.count!!)
            putString("url", event.ringTone.url)
            putString("hometype", event.ringTone.hometype)
            putInt("isVip", event.ringTone.isVip!!)
            putString("datatype", event.ringTone.datatype)
        }
        val fragment = DetailPlayMusicFragment()
        fragment.arguments = bundle
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out)
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
//        val layoutManager = binding.mainRecyclerView.layoutManager as LinearLayoutManager
//            layoutManager.scrollToPosition(0)
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


    override fun onItemClick(url: String, title: String, time: Int) {
        sendDataToActivity(url, title, time)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBusUnRegister()
    }
}
