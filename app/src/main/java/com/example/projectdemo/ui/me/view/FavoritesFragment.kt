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
import com.example.projectdemo.databinding.FragmentFavoritesBinding
import com.example.projectdemo.event.EventUnFavorite
import com.example.projectdemo.ui.me.adapter.FavoritesAdapter
import com.example.projectdemo.ui.me.viewmodel.FavoritesViewModel
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: FavoritesAdapter
    private val viewModel: FavoritesViewModel by viewModels()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.recyclerViewFavorites.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        viewModel.getAllMusic().observe(viewLifecycleOwner) { ringTones ->
            val data = ringTones.filter { it.isFavorite == 1 }
            adapter = FavoritesAdapter(data)
            binding.recyclerViewFavorites.adapter = adapter
        }
    }

    @Subscribe
    fun onDeleteItem(eventUnFavorite: EventUnFavorite) {
        lifecycleScope.launch {
            viewModel.updateFavoriteWhenHaveDownload(eventUnFavorite.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBusUnRegister()
    }
}