package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDataBase
import com.example.myapplication.entity.Photo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyViewModel(private val appDataBase: AppDataBase) : ViewModel() {

    private var _routeLink = MutableStateFlow<String?>(null)
    val routeLink = _routeLink.asStateFlow()

    suspend fun setRoute(route: String?) {
        _routeLink.value = route
        delay(50)
        _routeLink.value = null
    }

    val allPhotos = this.appDataBase.photoDao().getAll()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun onPhotoMake(newUri: String, newDate: String) {
        viewModelScope.launch {
            appDataBase.photoDao().upsertPhoto(
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
                appDataBase.photoDao().deleteAllPhotos(it)
            }
        }
    }

    fun deleteOnePhoto(photo: Photo) {
        viewModelScope.launch {
            appDataBase.photoDao().deleteOnePhoto(photo)
        }
    }
}

