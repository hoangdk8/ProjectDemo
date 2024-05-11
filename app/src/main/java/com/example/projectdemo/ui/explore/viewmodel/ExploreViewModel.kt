package com.example.projectdemo.ui.explore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.data.dataclass.DataCategories
import com.example.projectdemo.data.dataclass.DataDetailCategories
import com.example.projectdemo.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _data = MutableLiveData<List<DataCategories.Data>?>()
    val data: LiveData<List<DataCategories.Data>?> get() = _data

    private val _dataDetail = MutableLiveData<List<DataDetailCategories.Data>>()
    val dataDetail: LiveData<List<DataDetailCategories.Data>> get() = _dataDetail

    private val _hasNext = MutableLiveData<DataDetailCategories>()
    val hasNext: LiveData<DataDetailCategories> get() = _hasNext

    private val _categoryCode = MutableLiveData<DataCategories.Data>()
    val categoryCode: LiveData<DataCategories.Data> get() = _categoryCode


    init {
        _dataDetail.value = mutableListOf()
        fetchData()
    }



    fun fetchData() {
        viewModelScope.launch {
            try {
                val response = categoryRepository.getListCategory()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _data.value = it.data as List<DataCategories.Data>?
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching items")
            }
        }
    }
    fun fetchDataCategoryCode() {
        viewModelScope.launch {
            try {
                val response = categoryRepository.getListCategory()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _categoryCode.value = it.data as DataCategories.Data?
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching items")
            }
        }
    }

    fun fetchDataDetail(cat: Int,offset:Int) {
        viewModelScope.launch {
            try {
                val response = categoryRepository.getListOfCategory(cat,offset)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val newData = it.data as List<DataDetailCategories.Data>
                        val currentData = _dataDetail.value.orEmpty().toMutableList()
                        currentData.addAll(newData)
                        _dataDetail.value = currentData
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching items")
            }
        }
    }
    fun fetchHasNext(cat: Int,offset:Int) {
        viewModelScope.launch {
            try {
                val response = categoryRepository.getListOfCategory(cat,offset)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _hasNext.value = it
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching items")
            }
        }
    }
}