package com.example.projectdemo.ui.me.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectdemo.databinding.FragmentDownloadedBinding
import com.example.projectdemo.event.EventUnDownload
import com.example.projectdemo.ui.me.adapter.DownloadAdapter
import com.example.projectdemo.ui.me.viewmodel.DownloadViewModel
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class DownloadedFragment : Fragment() {
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
            adapter = DownloadAdapter(data)
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
}