package com.example.myapplication.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.IS_FIRST_RUN
import com.example.myapplication.data.States
import com.example.myapplication.data.dto.DetailInfoDto
import com.example.myapplication.domain.usecase.GetInfoUseCase
import com.example.myapplication.domain.usecase.GetLocationUseCase
import com.example.myapplication.domain.usecase.SearchUseCase
import com.example.myapplication.entity.models.Feature
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getInfoUseCase: GetInfoUseCase,
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _states = MutableStateFlow<States>(States.Success)
    val states = _states.asStateFlow()

    private val _location = MutableStateFlow<LatLng?>(null)
    val location = _location.asStateFlow()

    private val _leftBottomPoint = MutableStateFlow<LatLng?>(null)
    val leftBottomPoint = _leftBottomPoint.asStateFlow()

    private val _leftTopPoint = MutableStateFlow<LatLng?>(null)
    private val leftTopPoint = _leftTopPoint.asStateFlow()

    private val _rightTopPoint = MutableStateFlow<LatLng?>(null)
    val rightTopPoint = _rightTopPoint.asStateFlow()

    private val _rightBottomPoint = MutableStateFlow<LatLng?>(null)
    private val rightBottomPoint = _rightBottomPoint.asStateFlow()

    private val _polygonPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val polygonPoints = _polygonPoints.asStateFlow()

    private val _place = MutableStateFlow<DetailInfoDto?>(null)
    val place = _place.asStateFlow()

    private var _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var _foundPlaces =
        MutableStateFlow<List<Feature>?>(null)
    val foundPlaces = _foundPlaces.asStateFlow()

    init {
        viewModelScope.launch {
            getLocationUseCase.invoke().collect {
                _location.value = it
            }
        }
    }

    fun checkFirstRun(error: String, prefs: SharedPreferences) {
        viewModelScope.launch {
            val isFirstRun = prefs.getBoolean(IS_FIRST_RUN, true)
            Log.d("isFirstRun", "$isFirstRun")
            if (isFirstRun) {
                _states.value = States.Error
                _error.value = error
                prefs.edit().putBoolean(IS_FIRST_RUN, false).apply()
            }
        }
    }

    fun changeState(value: States) {
        viewModelScope.launch {
            _states.value = value
        }
    }

    fun getInfo(id: String) {
        viewModelScope.launch {
            try {
                _place.value = getInfoUseCase.execute(id)
            } catch (e: Exception) {
                _states.value = States.Error
                _error.value = e.message
            }
        }
    }

    fun setLeftBottomPoint(coordinates: LatLng) {
        viewModelScope.launch {
            _leftBottomPoint.value = coordinates
        }
    }

    fun clearPolygonPoints() {
        viewModelScope.launch {
            _states.value = States.Success
            _foundPlaces.value = null
            _searchText.value = ""
            delay(50)
            _polygonPoints.value = emptyList()
            _leftTopPoint.value = null
            _leftBottomPoint.value = null
            _rightBottomPoint.value = null
            _rightTopPoint.value = null
        }
    }

    fun setAllPointsOfPolygon(coordinates: LatLng) {
        viewModelScope.launch {
            _rightTopPoint.value = coordinates // set right top point

            combine(
                leftBottomPoint,
                rightTopPoint,
                leftTopPoint,
                rightBottomPoint
            ) { leftBottomPointValue, rightTopPointValue, leftTopPointValue, rightBottomPointValue ->
                if (leftBottomPointValue != null && rightTopPointValue != null) {
                    _leftTopPoint.value = LatLng( // set left top point
                        leftBottomPointValue.latitude,
                        rightTopPointValue.longitude
                    )
                    _rightBottomPoint.value = LatLng( // set right bottom point
                        rightTopPointValue.latitude,
                        leftBottomPointValue.longitude
                    )

                    // create list of points for polygon
                    if (leftTopPointValue != null && rightBottomPointValue != null) {
                        val updatedPolygonPoints = listOf(
                            leftTopPointValue,
                            leftBottomPointValue,
                            rightBottomPointValue,
                            rightTopPointValue
                        )
                        _polygonPoints.value = updatedPolygonPoints
                    }
                }
            }.collect()
        }
    }

    fun search(
        query: String,
        leftBottomPoint: LatLng?,
        rightTopPoint: LatLng?,
        emptyListError: String,
        searchPointsError: String
    ) {
        viewModelScope.launch {
            if (query.length > 2) {
                _states.value = States.Success
                _foundPlaces.value = emptyList()
                try {
                    _states.value = States.Loading
                    val list = searchUseCase.execute(
                        query,
                        leftBottomPoint?.longitude,
                        leftBottomPoint?.latitude,
                        rightTopPoint?.longitude,
                        rightTopPoint?.latitude
                    )
                    if (list.isEmpty()) {
                        _states.value = States.Error
                        _error.value = emptyListError
                        _foundPlaces.value = emptyList()
                    } else {
                        _states.value = States.Success
                        _foundPlaces.value = list
                    }
                } catch (_: HttpException) {
                    _states.value = States.Error
                    _error.value = searchPointsError
                    _foundPlaces.value = emptyList()
                } catch (e: Exception) {
                    _states.value = States.Error
                    _error.value = e.message
                }
            }
        }
    }

    fun onQueryChange(text: String) {
        viewModelScope.launch {
            _searchText.value = text
        }
    }

    fun onExpandedChange() {
        viewModelScope.launch {
            _isSearching.value = !_isSearching.value
            if (!_isSearching.value) {
                onQueryChange("")
            }
        }
    }
}