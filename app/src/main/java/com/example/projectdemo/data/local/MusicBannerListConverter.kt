package com.example.projectdemo.data.local

import androidx.room.TypeConverter
import com.example.projectdemo.data.dataclass.MusicBanner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MusicBannerListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<MusicBanner>? {
        val listType = object : TypeToken<List<MusicBanner>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<MusicBanner>?): String {
        return gson.toJson(list)
    }
}
