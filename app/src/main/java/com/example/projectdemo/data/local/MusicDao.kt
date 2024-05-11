package com.example.projectdemo.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectdemo.data.dataclass.DataDefaultRings

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertData(ringTone: DataDefaultRings.RingTone)

    @Query("SELECT * FROM music_table")
    fun getAllData(): LiveData<List<DataDefaultRings.RingTone>>
    @Query("SELECT isFavorite FROM music_table WHERE id = :id")
    fun getIsFavoriteById(id: Int): LiveData<Int>

    @Query("SELECT isDownload FROM music_table WHERE id = :id")
    fun getIsDownloadById(id: Int): LiveData<Int>
    @Query("DELETE FROM music_table WHERE id = :id")
    fun deleteDataById(id: Int)
    @Query("UPDATE music_table SET isDownload = 1 WHERE id = :id")
    suspend fun updateDownloadStatus(id: Int)

    @Query("UPDATE music_table SET isFavorite = 1 WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int)

    @Query("UPDATE music_table SET isDownload = 0 WHERE id = :id")
    suspend fun updateDownloadWhenHaveFavorite(id: Int)

    @Query("UPDATE music_table SET isFavorite = 0 WHERE id = :id")
    suspend fun updateFavoriteWhenHaveDownload(id: Int)

}