package com.example.myapplication.domain

import kotlinx.coroutines.flow.Flow

class GetsSpeedUseCase(
    private val locationService: ILocationService
) {
    operator fun invoke(): Flow<Int?> = locationService.requestSpeed()
}