package com.example.projectdemo.data.repository

import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.ListHomeModel
import com.example.projectdemo.data.local.MusicDao
import com.example.projectdemo.domain.ApiService
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiService: ApiService,
    private val musicDao: MusicDao
) {
    suspend fun getListSongDefault(page: Int): Response<ListHomeModel> {
        return apiService.dataDefaultRings(page)
    }

    fun getAllMusic() = musicDao.getAllData()

    suspend fun getIsDownloadById(id: Int) = musicDao.getIsDownloadById(id)
    suspend fun getIsFavoriteById(id: Int) = musicDao.getIsFavoriteById(id)
    suspend fun insertMusic(ringTone: DataDefaultRings.RingTone) = musicDao.insertData(ringTone)

    suspend fun deleteById(id: Int) = musicDao.deleteDataById(id)
    suspend fun updateDownloadStatus(id: Int) = musicDao.updateDownloadStatus(id)
    suspend fun updateFavoriteStatus(id: Int) = musicDao.updateFavoriteStatus(id)

    suspend fun updateDownloadWhenHaveFavorite(id: Int) = musicDao.updateDownloadWhenHaveFavorite(id)
    suspend fun updateFavoriteWhenHaveDownload(id: Int) = musicDao.updateFavoriteWhenHaveDownload(id)
}
