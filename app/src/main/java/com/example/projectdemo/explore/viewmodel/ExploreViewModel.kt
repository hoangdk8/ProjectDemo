package com.example.projectdemo.explore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.dataclass.ApiHelper
import com.example.projectdemo.dataclass.DataCategories
import com.example.projectdemo.dataclass.DataDefaultRings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val apiHelper: ApiHelper
) : ViewModel() {
    private val _data = MutableLiveData<List<DataCategories.Data>>()
    val data: LiveData<List<DataCategories.Data>> get() = _data
    fun fetchData() {
        viewModelScope.launch {
            val response = apiHelper.dataCategories()
            _data.value = response.data as List<DataCategories.Data>?
        }
    }
}