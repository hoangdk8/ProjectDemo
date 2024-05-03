package com.example.projectdemo.data.dataclass


import com.google.gson.annotations.SerializedName

data class DataDefaultRings(
    @SerializedName("data")
    var `data`: List<Data?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("pageId")
    var pageId: String?,
    @SerializedName("status")
    var status: Int?
) {
    data class Data(
        @SerializedName("categories")
        var categories: String?,
        @SerializedName("count")
        var count: Int?,
        @SerializedName("datatype")
        var datatype: String?,
        @SerializedName("duration")
        var duration: Int?,
        @SerializedName("hometype")
        var hometype: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("isVip")
        var isVip: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("url")
        var url: String?
    )
}