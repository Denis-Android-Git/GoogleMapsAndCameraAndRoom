package com.example.myapplication.domain

import com.example.myapplication.data.PlacesRepository
import com.example.myapplication.entity.Feature

class GetPlacesUseCase(
    lon: Double,
    lat: Double
) {
    private val placesRepository = PlacesRepository(lon, lat)
    suspend fun execute(): List<Feature> {
        return placesRepository.getPlacesDto()
    }
}