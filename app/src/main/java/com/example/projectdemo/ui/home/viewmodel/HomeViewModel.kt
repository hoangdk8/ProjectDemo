package com.example.projectdemo.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.MusicBanner
import com.example.projectdemo.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
) : ViewModel() {
    private val _itemList = MutableLiveData<List<DataDefaultRings.RingTone>>()
    val itemList: LiveData<List<DataDefaultRings.RingTone>> = _itemList

    private val _itemListFilter = MutableLiveData<List<DataDefaultRings.RingTone>>()
    val itemListFilter: LiveData<List<DataDefaultRings.RingTone>> = _itemListFilter
    var currentPage = 0
    var isLoading = false

    init {
        prepareData()
        getItems(currentPage)
    }

    fun prepareData() {
        val musicTopList = ArrayList<MusicBanner>()
        musicTopList.add(MusicBanner("[KBS] 오아시스 OST", R.drawable.image1))
        musicTopList.add(MusicBanner("[SBS] 낭만닥터 김사부 OST", R.drawable.image2))
        musicTopList.add(MusicBanner("더 글로리 OST", R.drawable.image3))

        val dataList = mutableListOf<DataDefaultRings.RingTone>()
        dataList.add(
            DataDefaultRings.RingTone(
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                musicTopList
            )
        )
        _itemList.value = dataList
    }


    fun getItemsFilter(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getListSongDefault(page)
                if (response.isSuccessful) {
                    response.body()?.let { listHomeModel ->
                        _itemListFilter.value = listHomeModel.items
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching items")
            }
        }
    }

    fun getItems(page: Int) {
        if (isLoading) return
        isLoading = true
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
                isLoading = false
            }
        }
    }
}
