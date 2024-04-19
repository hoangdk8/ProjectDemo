//package com.example.project_figma.ui.Home
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.project_figma.R
//import com.example.project_figma.data.model.ItemModel
//import com.example.project_figma.data.model.ListHomeModel
//import com.example.project_figma.data.repository.HomeRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import timber.log.Timber
//import javax.inject.Inject
//
//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val repository: HomeRepository
//) : ViewModel() {
//
//    private val _itemList = MutableLiveData<ListHomeModel>()
//    val itemList: LiveData<ListHomeModel> = _itemList
//    var currentPage = 0
//    var totalPages = 1
//
//    init {
//        getItems()
//    }
//
//    fun getItemListHorizontal(): List<ItemModel> {
//        return listOf(
//            ItemModel(R.drawable.frame_funny, "Title 1"),
//            ItemModel(R.drawable.frame_funny, "Title 2"),
//            ItemModel(R.drawable.frame_funny, "Title 3"),
//            ItemModel(R.drawable.frame_funny, "Title 2"),
//        )
//    }
//
//    fun loadMoreItems() {
//        currentPage++
//        getItems(currentPage)
//    }
//
//    fun getItems(page: Int = currentPage) {
//        viewModelScope.launch {
//            try {
//                val response = repository.getListSongDefault(page)
//                if (response.isSuccessful) {
//                    response.body()?.let { listHomeModel ->
//                        _itemList.value = listHomeModel
//                        totalPages = listHomeModel.pageId
//                        if(currentPage < totalPages){
//                            currentPage++
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                Timber.e(e, "Error fetching items")
//            }
//        }
//    }
//}
