package com.example.myapplication.data

import com.example.myapplication.entity.Feature

sealed class States {
    data object Loading : States()
    data class Error(val error: String?) : States()
    data class Success(val list: List<Feature>?) : States()
}