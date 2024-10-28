package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IntentViewModel : ViewModel() {
    private var _routeLink = MutableStateFlow<String?>(null)
    val routeLink = _routeLink.asStateFlow()

    fun setRoute(route: String?) {
        viewModelScope.launch {
            _routeLink.value = route
            delay(50)
            _routeLink.value = null
        }
    }

}