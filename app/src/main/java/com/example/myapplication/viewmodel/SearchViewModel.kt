package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.States
import com.example.myapplication.domain.SearchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchUseCase: SearchUseCase
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _states = MutableStateFlow<States>(States.Success(emptyList()))
    val states = _states.asStateFlow()

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
    }
}