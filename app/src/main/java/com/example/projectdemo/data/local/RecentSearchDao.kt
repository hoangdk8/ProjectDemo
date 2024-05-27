package com.example.projectdemo.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.RecentSearch

@Dao
interface RecentSearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertData(recentSearch: RecentSearch)

    @Query("SELECT * FROM recent_table")
    fun getAllData(): LiveData<List<RecentSearch>>

    @Query("DELETE FROM recent_table WHERE name = :itemName")
    fun deleteDataById(itemName: String)

    @Query("SELECT COUNT(*) FROM recent_table WHERE name = :itemName")
    fun getItemCountByName(itemName: String): Int

}