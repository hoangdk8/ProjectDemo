package com.example.projectdemo.ui.detailplaymusic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPlayMusicViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {


    fun addMusic(ringTone: DataDefaultRings.RingTone) {
        viewModelScope.launch {
            homeRepository.insertMusic(ringTone)
        }
    }

    suspend fun getIsDownloadById(id: Int) = homeRepository.getIsDownloadById(id)
    suspend fun getIsFavoriteById(id: Int) = homeRepository.getIsFavoriteById(id)
    suspend fun deleteIsFavoriteById(id: Int) = homeRepository.deleteById(id)
    suspend fun updateDownloadStatus(id: Int) = homeRepository.updateDownloadStatus(id)
    suspend fun updateFavoriteStatus(id: Int) = homeRepository.updateFavoriteStatus(id)
    suspend fun updateFavoriteWhenHaveDownload(id: Int) = homeRepository.updateFavoriteWhenHaveDownload(id)
    suspend fun updateDownloadWhenHaveFavorite(id: Int) = homeRepository.updateDownloadWhenHaveFavorite(id)
}