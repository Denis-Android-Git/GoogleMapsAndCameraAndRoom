package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DetailInfoDto
import com.example.myapplication.domain.GetInfoUseCase
import com.example.myapplication.domain.GetPlacesUseCase
import com.example.myapplication.entity.Feature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val getPlacesUseCase: GetPlacesUseCase,
    private val getInfoUseCase: GetInfoUseCase
) : ViewModel() {
    private var _places = MutableStateFlow<List<Feature>>(emptyList())
    val places = _places.asStateFlow()

    private var _detailInfo = MutableStateFlow<DetailInfoDto?>(null)
    val detailInfo = _detailInfo.asStateFlow()

    fun getPlaces(
        lon: Double,
        lat: Double
    ) {
        viewModelScope.launch {
            _places.value = getPlacesUseCase.execute(lon, lat)
        }
    }

    fun getInfo(
        xid: String
    ) {
        viewModelScope.launch {
            _detailInfo.value = getInfoUseCase.execute(xid)
        }
    }
}