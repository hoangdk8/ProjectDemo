package com.example.projectdemo.dataclass

import retrofit2.http.GET
import retrofit2.http.Query

interface DetailCategoriesApi {
    @GET("ringtones?lang=en_VN&offset=0&limit=40&order=downDate_desc&token=12345")
    suspend fun dataDetailCategories(@Query("cat") cat: Int): DataDetailCategories
}