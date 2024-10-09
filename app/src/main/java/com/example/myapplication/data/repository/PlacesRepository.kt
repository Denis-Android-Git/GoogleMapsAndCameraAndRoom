package com.example.myapplication.data.repository

import com.example.myapplication.data.api.RetrofitAndApi
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