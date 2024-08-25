package com.example.myapplication.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DetailInfoDto
import com.example.myapplication.domain.GetInfoUseCase
import com.example.myapplication.domain.GetLocationUseCase
import com.example.myapplication.domain.GetPlacesUseCase
import com.example.myapplication.domain.GetsSpeedUseCase
import com.example.myapplication.entity.Feature
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
class MapViewModel(
    private val getPlacesUseCase: GetPlacesUseCase,
    private val getInfoUseCase: GetInfoUseCase,
    private val getsSpeedUseCase: GetsSpeedUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {
    private var _places = MutableStateFlow<List<Feature>>(emptyList())
    val places = _places.asStateFlow()

    private var _detailInfo = MutableStateFlow<DetailInfoDto?>(null)
    val detailInfo = _detailInfo.asStateFlow()

    private var _location = MutableStateFlow<LatLng?>(null)
    val location = _location.asStateFlow()

    private var _speed = MutableStateFlow<Int?>(null)
    val speed = _speed.asStateFlow()

    private var _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            getsSpeedUseCase.invoke().collect {
                _speed.value = it
            }
        }
    }

    init {
        viewModelScope.launch {
            getLocationUseCase.invoke().collect {
                _location.value = it
            }
        }
    }

    fun getPlaces(
        lon: Double,
        lat: Double
    ) {
        viewModelScope.launch {
            try {
                _places.value = getPlacesUseCase.execute(lon, lat)
            } catch (e: Exception) {
                _error.value = "No connection"
            }
        }
    }

    fun getInfo(
        xid: String
    ) {
        viewModelScope.launch {
            try {
                _detailInfo.value = getInfoUseCase.execute(xid)
            } catch (e: Exception) {
                _error.value = "No connection"
            }
        }
    }
}