package com.example.myapplication.data

import com.example.myapplication.entity.Feature
import com.example.myapplication.entity.Places

class PlacesDto(
    override val features: List<Feature>,
    override val type: String
) : Places