package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailScreenViewModel : ViewModel() {
    val showDelete = mutableStateOf(false)
    val image = mutableStateOf("")

    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation = _showDeleteConfirmation.asStateFlow()

    fun setShowDeleteConfirmationValue(value: Boolean) {
        viewModelScope.launch {
            _showDeleteConfirmation.value = value
        }
    }

    private val _showImage = MutableStateFlow(true)
    val showImage = _showImage.asStateFlow()

    fun setShowImageValue(value: Boolean) {
        viewModelScope.launch {
            _showImage.value = value
        }
    }

    init {
        viewModelScope.launch {
            delay(100)
            Log.d("image_in_vm", image.value)
            showDelete.value = !image.value.contains("https")
        }
    }
}