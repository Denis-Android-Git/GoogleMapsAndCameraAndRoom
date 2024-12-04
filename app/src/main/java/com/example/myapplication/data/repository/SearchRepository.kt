package com.example.myapplication.data.repository

import com.example.myapplication.data.api.RetrofitAndApi
import com.example.myapplication.entity.models.Feature

class SearchRepository(
    private val retrofitAndApi: RetrofitAndApi.PlacesApi
) {
    suspend fun search(
        name: String,
        lon_min: Double?,
        lat_min: Double?,
        lon_max: Double?,
        lat_max: Double?
    ): List<Feature> {
        return retrofitAndApi.search(name, lon_min, lat_min, lon_max, lat_max).features
    }
}