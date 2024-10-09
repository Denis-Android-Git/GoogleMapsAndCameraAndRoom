package com.example.myapplication.data.repository

import com.example.myapplication.data.api.RetrofitAndApi
import com.example.myapplication.entity.Feature

class SearchRepository(
    private val retrofitAndApi: RetrofitAndApi.PlacesApi
) {
    suspend fun search(name: String): List<Feature> {
        return retrofitAndApi.search(name).features
    }
}