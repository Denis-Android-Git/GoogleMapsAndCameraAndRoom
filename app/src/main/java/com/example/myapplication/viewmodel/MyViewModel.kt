package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.PhotoDao
import com.example.myapplication.entity.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyViewModel(private val photoDao: PhotoDao) : ViewModel() {

    private var _routeLink = MutableStateFlow<String?>(null)
    val routeLink = _routeLink.asStateFlow()

    fun setRoute(route: String?) {
        _routeLink.value = route
    }

    val allPhotos = this.photoDao.getAll()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun onPhotoMake(newUri: String, newDate: String) {
        viewModelScope.launch {
            photoDao.upsertPhoto(
                Photo(
                    uri = newUri,
                    date = newDate
                )
            )
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            allPhotos.value.let {
                photoDao.deleteAllPhotos(it)
            }
        }
    }

    fun deleteOnePhoto(photo: Photo) {
        viewModelScope.launch {
            photoDao.deleteOnePhoto(photo)
        }
    }
}

