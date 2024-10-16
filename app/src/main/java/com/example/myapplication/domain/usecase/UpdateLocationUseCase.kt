package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.ILocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class UpdateLocationUseCase(
    private val locationService: ILocationService
) {
    operator fun invoke(): Flow<LatLng?> = locationService.requestLocationUpdates()
}