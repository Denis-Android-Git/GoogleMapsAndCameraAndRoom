package com.example.myapplication.data

import com.example.myapplication.entity.Feature

class PlacesRepository(
    private val lon: Double,
    private val lat: Double
) {
    private val retrofitAndApi = RetrofitAndApi()

    suspend fun getPlacesDto(): List<Feature> {
        return retrofitAndApi.api.getPlaces(lon, lat).features
    }

}