package com.example.projectdemo.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.RecentSearch
import com.example.projectdemo.repository.HomeRepository
import com.example.projectdemo.repository.RecentSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recentSearchRepository: RecentSearchRepository,
    private val repository: HomeRepository
) : ViewModel() {
    private val _itemList = MutableLiveData<List<DataDefaultRings.RingTone>>()
    val itemList: LiveData<List<DataDefaultRings.RingTone>> = _itemList
    var currentPage = 0
    init {
        getData(currentPage)
    }

    fun getData(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getListSongDefault(page)
                if (response.isSuccessful) {
                    response.body()?.let { listHomeModel ->
                        val newDataItemList = mutableListOf<DataDefaultRings.RingTone>()
                        listHomeModel.items.forEachIndexed { index, homeModel ->
                            if ((index + 1) % 5 == 0 && index > 0) {
                                newDataItemList.add(
                                    DataDefaultRings.RingTone(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        1,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }
                            newDataItemList.add(homeModel)
                        }

                        val currentList = _itemList.value?.toMutableList() ?: mutableListOf()
                        currentList.addAll(newDataItemList)
                        _itemList.value = currentList
                        currentPage = page
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching items")
            } finally {
            }
        }
    }

    fun addRecent(recentSearch: RecentSearch) {
        viewModelScope.launch {
            recentSearchRepository.insertRecentSearch(recentSearch)
        }
    }

    suspend fun deleteIsFavoriteById(name: String) = recentSearchRepository.deleteById(name)
    fun getItemCountByName(itemName: String) = recentSearchRepository.getItemCountByName(itemName)
    fun getAllData() = recentSearchRepository.getAllData()
}