package com.example.projectdemo.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.dataclass.ApiHelper
import com.example.projectdemo.dataclass.DataDefaultRings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiHelper: ApiHelper
) : ViewModel() {
    private val _data = MutableLiveData<List<DataDefaultRings.Data>>()
    val data: LiveData<List<DataDefaultRings.Data>> get() = _data
    fun fetchData(page : Int) {
        viewModelScope.launch {
            val response = apiHelper.dataDefaultRings(page)
            _data.value = response.data as List<DataDefaultRings.Data>?
        }
    }
}
