package com.example.projectdemo.ui.me.viewmodel

import androidx.lifecycle.ViewModel
import com.example.projectdemo.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    fun getAllMusic() = homeRepository.getAllMusic()
    suspend fun deleteIsFavoriteById(id: Int) = homeRepository.deleteById(id)
    suspend fun updateDownloadWhenHaveFavorite(id: Int) = homeRepository.updateDownloadWhenHaveFavorite(id)
}