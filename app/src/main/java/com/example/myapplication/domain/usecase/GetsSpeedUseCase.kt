package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.ILocationService
import kotlinx.coroutines.flow.Flow

class GetsSpeedUseCase(
    private val locationService: ILocationService
) {
    operator fun invoke(): Flow<Int?> = locationService.requestSpeed()
}