package com.example.myapplication.data

import com.example.myapplication.entity.Feature

sealed class States {
    data object Loading : States()
    data object Error : States()
    data object Success : States()
}