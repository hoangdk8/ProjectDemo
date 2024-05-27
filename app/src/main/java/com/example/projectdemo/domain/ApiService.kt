package com.example.projectdemo.domain

import androidx.annotation.Keep
import com.example.projectdemo.data.dataclass.DataCategories
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.ListHomeModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

@Keep
interface ApiService {
    @Streaming
    @GET
    fun downloadFile(
        @Url url: String
    ): Call<ResponseBody>
    @GET("defaultrings/fr2019tkv2secv129?country=VN&config=false")
    suspend fun dataDefaultRings(@Query("page") page: Int): Response<ListHomeModel>

    @GET("categories?lang=en_VN&token=12345")
    suspend fun dataCategories(): Response<DataCategories>

    @GET("ringtones?lang=en_VN&limit=40&order=downDate_desc&token=12345")
    suspend fun dataDetailCategories(@Query("cat") cat: Int,@Query("offset") offset: Int): Response<DataDefaultRings>
}