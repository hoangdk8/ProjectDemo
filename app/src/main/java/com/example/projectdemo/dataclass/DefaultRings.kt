package com.example.projectdemo.dataclass

import retrofit2.http.GET
import retrofit2.http.Query

interface DefaultRings {
    @GET("defaultrings/fr2019tkv2secv129?country=VN&config=false")
    suspend fun dataDefaultRings(@Query("page") page: Int): DataDefaultRings
}