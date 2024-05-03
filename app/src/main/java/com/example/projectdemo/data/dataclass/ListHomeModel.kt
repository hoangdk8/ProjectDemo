package com.example.projectdemo.data.dataclass

import com.google.gson.annotations.SerializedName

data class ListHomeModel(
    @SerializedName("pageId")
    var pageId: Int,
    @SerializedName("data")
    val items: List<DataDefaultRings.Data>
)