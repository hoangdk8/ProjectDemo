package com.example.projectdemo.ui.me.viewmodel

import androidx.lifecycle.ViewModel
import com.example.projectdemo.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    fun getAllMusic() = homeRepository.getAllMusic()
    suspend fun deleteIsFavoriteById(id: Int) = homeRepository.deleteById(id)
    suspend fun updateFavoriteWhenHaveDownload(id: Int) = homeRepository.updateFavoriteWhenHaveDownload(id)
    suspend fun getIsDownloadById(id: Int) = homeRepository.getIsDownloadById(id)
    suspend fun getIsFavoriteById(id: Int) = homeRepository.getIsFavoriteById(id)
}