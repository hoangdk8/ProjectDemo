package com.example.projectdemo.data.dataclass


import com.google.gson.annotations.SerializedName

data class DataCategories(
    @SerializedName("data")
    var `data`: List<Data?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("type")
    var type: String?
) {
    data class Data(
        @SerializedName("count")
        var count: Int?,
        @SerializedName("countries")
        var countries: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("url")
        var url: String?
    )
}