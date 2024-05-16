package com.example.projectdemo.data.dataclass


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class DataDefaultRings(
    @SerializedName("data")
    var `data`: List<RingTone?>?,
    @SerializedName("hasnext")
    var hasnext: Boolean?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("nextoffset")
    var nextoffset: Int?,
    @SerializedName("pageId")
    var pageId: String?,
    @SerializedName("status")
    var status: Int?
) {
    @Entity(tableName = "music_table")
    data class RingTone(
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
        @PrimaryKey
        @SerializedName("id")
        var id: Int?,
        @SerializedName("isVip")
        var isVip: Int?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("url")
        var url: String?,
        @SerializedName("online")
        var online: Boolean?,
        @SerializedName("isFavorite")
        var isFavorite: Int?,
        @SerializedName("isDownload")
        var isDownload: Int?,
        @SerializedName("colections")
        var collections:Int?
    )
}