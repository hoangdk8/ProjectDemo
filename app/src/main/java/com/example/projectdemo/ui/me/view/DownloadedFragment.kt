package com.example.projectdemo.ui.me.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentDownloadedBinding
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.eventbus.EventUnDownload
import com.example.projectdemo.ui.detailplaymusic.DetailPlayMusicFragment
import com.example.projectdemo.ui.me.adapter.DownloadAdapter
import com.example.projectdemo.ui.me.viewmodel.DownloadViewModel
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@AndroidEntryPoint
class DownloadedFragment : Fragment(), DetailPlayMusic {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var binding: FragmentDownloadedBinding
    private lateinit var adapter: DownloadAdapter
    private val viewModel: DownloadViewModel by viewModels()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.recyclerViewDownload.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        viewModel.getAllMusic().observe(viewLifecycleOwner) { ringTones ->
            val data = ringTones.filter { it.isDownload == 1 }
            adapter = DownloadAdapter(data, exoPlayerManager, this)
            binding.recyclerViewDownload.adapter = adapter
        }

    }

    @Subscribe
    fun onDeleteItem(eventUnDownload: EventUnDownload) {
        lifecycleScope.launch {
            viewModel.updateDownloadWhenHaveFavorite(eventUnDownload.id)
        }

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