package com.example.projectdemo.repository

import com.example.projectdemo.data.dataclass.DataCategories
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.domain.ApiService
import retrofit2.Response
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getListCategory(): Response<DataCategories> {
        return apiService.dataCategories()
    }

    suspend fun getListOfCategory(cat: Int,offset:Int): Response<DataDefaultRings> {
        return apiService.dataDetailCategories(cat,offset)
    }
}
