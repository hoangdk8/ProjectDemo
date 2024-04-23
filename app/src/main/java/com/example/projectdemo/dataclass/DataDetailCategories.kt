package com.example.projectdemo.dataclass


import com.google.gson.annotations.SerializedName

data class DataDetailCategories(
    @SerializedName("data")
    var `data`: List<Data?>?,
    @SerializedName("hasnext")
    var hasnext: Boolean?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("nextoffset")
    var nextoffset: Int?,
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
        @SerializedName("id")
        var id: Int?,
        @SerializedName("isVip")
        var isVip: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("online")
        var online: Boolean?,
        @SerializedName("url")
        var url: String?
    )
}