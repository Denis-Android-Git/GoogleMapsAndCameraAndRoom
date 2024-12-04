package com.example.myapplication.data.dto

import com.example.myapplication.entity.models.Feature
import com.example.myapplication.entity.models.Places

class PlacesDto(
    override val features: List<Feature>,
    override val type: String
) : Places