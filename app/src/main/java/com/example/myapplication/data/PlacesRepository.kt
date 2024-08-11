package com.example.myapplication.data

import com.example.myapplication.entity.Feature

class PlacesRepository(
    private val retrofitAndApi: RetrofitAndApi.PlacesApi
) {
    suspend fun getPlacesDto(
        lon: Double,
        lat: Double
    ): List<Feature> {
        return retrofitAndApi.getPlaces(lon, lat).features
    }

}