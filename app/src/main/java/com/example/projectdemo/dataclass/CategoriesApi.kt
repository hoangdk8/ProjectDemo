package com.example.projectdemo.dataclass

import retrofit2.http.GET

interface CategoriesApi {
    @GET("categories?lang=en_VN&token=12345")
    suspend fun dataCategories(): DataCategories
}