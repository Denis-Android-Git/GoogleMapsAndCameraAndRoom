package com.example.myapplication.domain

import kotlinx.coroutines.flow.Flow

interface ConnectService {
    val isConnected: Flow<Boolean>
}