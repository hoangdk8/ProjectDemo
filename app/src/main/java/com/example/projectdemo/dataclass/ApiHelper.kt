package com.example.projectdemo.dataclass

import javax.inject.Inject

class ApiHelper @Inject constructor(
    private val defaultRings: DefaultRings,
    private val categoriesApi: CategoriesApi,
    private val detailCategoriesApi: DetailCategoriesApi
) {
    suspend fun dataDefaultRings(page: Int): DataDefaultRings {
        return defaultRings.dataDefaultRings(page)
    }

    suspend fun dataCategories(): DataCategories {
        return categoriesApi.dataCategories()
    }
    suspend fun dataDetailCategories(cat:Int): DataDetailCategories {
        return detailCategoriesApi.dataDetailCategories(cat)
    }
}