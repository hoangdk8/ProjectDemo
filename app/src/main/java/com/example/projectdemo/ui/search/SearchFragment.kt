package com.example.projectdemo.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.RecentSearch
import com.example.projectdemo.databinding.FragmentSearchBinding
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.eventbus.EventSearch
import com.example.projectdemo.ui.home.adapter.HomeAdapter
import com.example.projectdemo.ui.home.viewmodel.HomeViewModel
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import com.example.projectdemo.untils.hideKeyboard
import com.example.projectdemo.untils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), DetailPlayMusic {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: HomeViewModel by viewModels()
    private val viewModelSearch: SearchViewModel by viewModels()
    private lateinit var adapterSearch: SearchAdapter
    private lateinit var adapterRecent: RecentSearchAdapter
    private lateinit var adapter: HomeAdapter
    private lateinit var ringtoneArrayList: ArrayList<DataDefaultRings.RingTone>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventBusRegister()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        showTrendingSearch()
        showResults()
    }

    private fun showResults() {
        ringtoneArrayList = ArrayList()
        viewModelSearch.itemList.observe(requireActivity(), Observer { it ->
            ringtoneArrayList.addAll(it)
            adapter = HomeAdapter(requireContext(),ringtoneArrayList, exoPlayerManager, this)

        })
    }
    @Subscribe
    fun onEventSearch(event: EventSearch) {
        binding.editTextSearch.setText(event.title)
    }

    private fun setupViews() {
        hideKeyboard()
        binding.txtCancelSearch.setOnClickListener {
            binding.editTextSearch.clearFocus()
        }
        binding.ctn.setOnClickListener {
            binding.editTextSearch.clearFocus()
        }
        binding.imageView.setOnClickListener {
            binding.editTextSearch.text = null
            binding.editTextSearch.clearFocus()
        }
        binding.editTextSearch.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard()
                if (binding.editTextSearch.text.count() >= 3) {
                    binding.txtTrending.text = getString(R.string.top_results)
                } else {
                    binding.txtTrending.text = getString(R.string.recent_search)
                }
                showRecentSearch()
            } else {
                hideKeyboard()
                if (binding.editTextSearch.text.isNullOrEmpty()) {
                    showTrendingSearch()
                }
            }
        }
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.editTextSearch.text.count() >= 3) {
                    binding.txtTrending.text = getString(R.string.top_results)
                } else {
                    binding.txtTrending.text = getString(R.string.recent_search)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Trước khi thay đổi văn bản
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (count>=3){
                    filter(binding.editTextSearch.text.toString())
                    binding.recyclerViewTrending.adapter = adapter
                }else{
                    binding.recyclerViewTrending.adapter = adapterRecent
                }

            }
        })

        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.recyclerViewTrending.adapter = adapter
                val existingItemCount =
                    viewModelSearch.getItemCountByName(binding.editTextSearch.text.toString())
                if (existingItemCount > 0) {

                } else if (binding.editTextSearch.text.toString().isNullOrBlank()) {

                } else {
                    viewModelSearch.addRecent(
                        RecentSearch(
                            binding.editTextSearch.text.trim().toString()
                        )
                    )
                }
                binding.editTextSearch.clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun filter(text: String) {
        val filteredList = ArrayList<DataDefaultRings.RingTone>()
        Log.d("hoang", "filter:$text ")
        for (item in ringtoneArrayList) {
            if (item.name?.contains(text, ignoreCase = true) == true) {
                filteredList.add(item)
            }
        }
        adapter.filterList(filteredList)
    }

    private fun showTrendingSearch() {
        viewModel.getItemsFilter(0)
        viewModel.itemListFilter.observe(requireActivity(), Observer { it ->
            val data = it.filter { it.hometype == "trends" }
            adapterSearch = SearchAdapter(data)
            binding.recyclerViewTrending.adapter = adapterSearch
        })
    }

    private fun showRecentSearch() {
        binding.txtTrending.text = getString(R.string.recent_search)
        viewModelSearch.getAllData().observe(requireActivity(), Observer { it ->
            adapterRecent = RecentSearchAdapter(it)
            binding.recyclerViewTrending.adapter = adapterRecent
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBusUnRegister()
    }

    override fun onShowDetailsMusic(ringTone: DataDefaultRings.RingTone) {
    }
}
