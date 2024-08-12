package com.example.myapplication.domain

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow

class GetsSpeedUseCase(
    private val locationService: ILocationService
) {
    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<Int?> = locationService.requestSpeed()
}