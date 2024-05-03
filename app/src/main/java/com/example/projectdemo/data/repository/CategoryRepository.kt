package com.example.projectdemo.data.repository

import com.example.projectdemo.data.dataclass.DataCategories
import com.example.projectdemo.data.dataclass.DataDetailCategories
import com.example.projectdemo.domain.ApiService
import retrofit2.Response
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getListCategory(): Response<DataCategories> {
        return apiService.dataCategories()
    }

    suspend fun getListOfCategory(cat: Int): Response<DataDetailCategories> {
        return apiService.dataDetailCategories(cat)
    }
}
