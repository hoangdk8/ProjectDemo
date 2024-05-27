package com.example.projectdemo.repository

import com.example.projectdemo.data.dataclass.RecentSearch
import com.example.projectdemo.data.local.RecentSearchDao
import javax.inject.Inject

class RecentSearchRepository @Inject constructor(
    private val recentSearchDao: RecentSearchDao
) {

    fun getAllData() = recentSearchDao.getAllData()

    suspend fun insertRecentSearch(recentSearch: RecentSearch) = recentSearchDao.insertData(recentSearch)

    suspend fun deleteById(name: String) = recentSearchDao.deleteDataById(name)
    fun getItemCountByName(itemName: String)=recentSearchDao.getItemCountByName(itemName)
}
