package com.example.projectdemo.explore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.dataclass.ApiHelper
import com.example.projectdemo.dataclass.DataDetailCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailCategoriesViewModel @Inject constructor(
    private val apiHelper: ApiHelper
) : ViewModel() {
    private val _data = MutableLiveData<List<DataDetailCategories.Data>>()
    val data: LiveData<List<DataDetailCategories.Data>> get() = _data
    fun fetchData(cat:Int) {
        viewModelScope.launch {
            val response = apiHelper.dataDetailCategories(cat)
            _data.value = response.data as List<DataDetailCategories.Data>?
        }
    }
}