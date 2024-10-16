package com.example.myapplication.domain.usecase

import com.example.myapplication.data.repository.SearchRepository
import com.example.myapplication.entity.Feature

class SearchUseCase(
    private val searchRepository: SearchRepository
) {
    suspend fun execute(name: String): List<Feature> {
        return searchRepository.search(name)
    }
}