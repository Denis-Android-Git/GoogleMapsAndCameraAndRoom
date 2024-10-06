package com.example.myapplication.domain

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class GetLocationUseCase(
    private val locationService: ILocationService
) {
    operator fun invoke(): Flow<LatLng?> = locationService.requestCurrentLocation()
}