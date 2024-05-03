package com.example.projectdemo.data.repository

import com.example.projectdemo.data.dataclass.ListHomeModel
import com.example.projectdemo.domain.ApiService
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService) {
     suspend fun getListSongDefault(page: Int): Response<ListHomeModel> {
        return apiService.dataDefaultRings(page)
    }
}