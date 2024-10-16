package com.example.myapplication.domain.usecase

import com.example.myapplication.data.repository.PlacesRepository
import com.example.myapplication.entity.Feature

class GetPlacesUseCase(
    private val placesRepository: PlacesRepository

) {
    suspend fun execute(
        lon: Double,
        lat: Double
    ): List<Feature> {
        return placesRepository.getPlacesDto(lon, lat)
    }
}