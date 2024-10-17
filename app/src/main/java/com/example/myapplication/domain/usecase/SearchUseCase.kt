package com.example.myapplication.domain.usecase

import com.example.myapplication.data.repository.SearchRepository
import com.example.myapplication.entity.Feature

class SearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend fun execute(
        name: String,
        lon_min: Double?,
        lat_min: Double?,
        lon_max: Double?,
        lat_max: Double?
    ): List<Feature> {
        return searchRepository.search(name, lon_min, lat_min, lon_max, lat_max)
    }
}