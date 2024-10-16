package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.States
import com.example.myapplication.domain.usecase.GetLocationUseCase
import com.example.myapplication.domain.usecase.SearchUseCase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchUseCase: SearchUseCase,
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _states = MutableStateFlow<States>(States.Success(emptyList()))
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

    fun setLeftBottomPoint(coordinates: LatLng) {
        viewModelScope.launch {
            _leftBottomPoint.value = coordinates
        }
    }

    fun clearPolygonPoints() {
        viewModelScope.launch {
            _polygonPoints.value = emptyList()
            _leftTopPoint.value = null
            _leftBottomPoint.value = null
            _rightBottomPoint.value = null
            _rightTopPoint.value = null
        }
    }

    fun setRightTopPoint(coordinates: LatLng) {
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


    fun onQueryChange(text: String) {
        _searchText.value = text
    }

    fun onExpandedChange() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onQueryChange("")
        }
    }

    init {
        viewModelScope.launch {
            searchText.collect {
                try {
                    if (it.length > 2) {
                        _states.value = States.Loading
                        val list = searchUseCase.execute(it)
                        if (list.isEmpty()) {
                            _states.value = States.Error("не найдено")
                        } else {
                            _states.value = States.Success(list)
                        }
                    }
                } catch (e: Exception) {
                    _states.value = States.Error(e.message)
                }
            }
        }
        viewModelScope.launch {
            getLocationUseCase.invoke().collect {
                _location.value = it
            }
        }
    }
}