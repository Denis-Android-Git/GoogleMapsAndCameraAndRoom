package com.example.myapplication.data

sealed class States {
    data object Loading : States()
    data object Error : States()
    data object Success : States()
}