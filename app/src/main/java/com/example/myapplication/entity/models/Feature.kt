package com.example.myapplication.entity.models

data class Feature(
    val geometry: Geometry,
    val id: String,
    val properties: Properties,
    val type: String
)