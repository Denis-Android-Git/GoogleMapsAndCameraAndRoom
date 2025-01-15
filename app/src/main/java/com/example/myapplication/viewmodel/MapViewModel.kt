package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.dto.DetailInfoDto
import com.example.myapplication.domain.ConnectService
import com.example.myapplication.domain.usecase.GetInfoUseCase
import com.example.myapplication.domain.usecase.GetLocationUseCase
import com.example.myapplication.domain.usecase.GetPlacesUseCase
import com.example.myapplication.domain.usecase.GetsSpeedUseCase
import com.example.myapplication.entity.models.Feature
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(
    private val getPlacesUseCase: GetPlacesUseCase,
    private val getInfoUseCase: GetInfoUseCase,
    private val getsSpeedUseCase: GetsSpeedUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    connectService: ConnectService
) : ViewModel() {

    val isConnected = connectService
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    private var _places = MutableStateFlow<List<Feature>>(emptyList())
    val places = _places.asStateFlow()
//        .onStart {
//            Log.d("onStartinitialGetPlaces", "onStart")
//            initialGetPlaces()
//        }
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000),
//            emptyList()
//        )

    private var _isFirstRun = MutableStateFlow(true)
    val isFirstRun = _isFirstRun.asStateFlow()

    fun updateIsFirstRun(value: Boolean) {
        viewModelScope.launch {
            _isFirstRun.value = value
        }
    }

    private var _detailInfo = MutableStateFlow<DetailInfoDto?>(null)
    val detailInfo = _detailInfo.asStateFlow()

    private var _location = MutableStateFlow<LatLng?>(null)
    val location = _location.asStateFlow()

    private var _cameraPosition =
        MutableStateFlow<CameraPosition?>(null) // (Pair<LatLng?, Float>(null, 15f))
    val cameraPosition = _cameraPosition.asStateFlow()

    private var _speed = MutableStateFlow<Int?>(null)
    val speed = _speed.asStateFlow()

    private var _error = MutableStateFlow<Int?>(null)
    val error = _error.asStateFlow()

    private val _showButton = MutableStateFlow(false)
    val showButton = _showButton.asStateFlow()

    private val _buttonText = MutableStateFlow(R.string.search_hear)
    val buttonText = _buttonText.asStateFlow()

    fun setShowButtonValue(value: Boolean) {
        viewModelScope.launch {
            _showButton.value = value
        }
    }

    private val _showText = MutableStateFlow(false)
    val showText = _showText.asStateFlow()

    fun setShowTextValue(value: Boolean) {
        viewModelScope.launch {
            _showText.value = value
        }
    }

    init {
        viewModelScope.launch {
            getsSpeedUseCase.invoke().collect {
                _speed.value = it
            }
        }
    }

    init {
        viewModelScope.launch {
            getLocationUseCase.invoke().collect { latLng ->
                latLng?.let {
                    val target = LatLng(it.latitude, it.longitude)
                    val position = CameraPosition(target, 15f, 0f, 0f)
                    _cameraPosition.value = position
                    _location.value = it
                }
            }
        }
    }

    fun updateCameraPosition(value: CameraPosition) {
        viewModelScope.launch {
            _cameraPosition.value = value
        }
    }

    init {
        //private fun initialGetPlaces() {
        viewModelScope.launch {
            isConnected.collectLatest {
                Log.d("isConnectedToInet", "$it")
                if (it) {
                    _error.value = null
                    location.collect { location ->
                        location?.let {
                            var retries = 0
                            while (retries < 5) {
                                try {
                                    _places.value =
                                        getPlacesUseCase.execute(it.longitude, it.latitude)
                                    break
                                } catch (_: Exception) {
                                    retries++
                                    if (retries == 5) {
                                        _showButton.value = true
                                        _buttonText.value = R.string.server_down
                                    }
                                }
                            }
                        }
                    }
                } else {
                    _error.value = R.string.no_connection
                    _places.value = emptyList()
                }
            }
        }
    }

    fun getPlaces(
        lon: Double,
        lat: Double
    ) {
        viewModelScope.launch {
            try {
                _buttonText.value = R.string.search_hear
                _error.value = null
                _places.value = getPlacesUseCase.execute(lon, lat)
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                _error.value = R.string.no_connection
            }
        }
    }

    fun clearPlaces() {
        viewModelScope.launch {
            _places.value = emptyList()
        }
    }

    fun getInfo(
        xid: String
    ) {
        viewModelScope.launch {
            try {
                _detailInfo.value = getInfoUseCase.execute(xid)
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                _error.value = R.string.no_connection
            }
        }
    }
}