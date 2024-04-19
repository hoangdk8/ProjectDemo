package com.example.projectdemo.dataclass

import javax.inject.Inject

class ApiHelper @Inject constructor(private val defaultRings: DefaultRings,private val categoriesApi: CategoriesApi) {
    suspend fun dataDefaultRings(page: Int): DataDefaultRings {
        return defaultRings.dataDefaultRings(page)
    }
    suspend fun dataCategories(): DataCategories{
        return categoriesApi.dataCategories()
    }
}