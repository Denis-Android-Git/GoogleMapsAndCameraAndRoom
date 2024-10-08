package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun onQueryChange(text: String) {
        _searchText.value = text
    }

    fun onExpandedChange() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            onQueryChange("")
        }
    }
}