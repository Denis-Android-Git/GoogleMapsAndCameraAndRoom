package com.example.myapplication.entity

data class Feature(
    val geometry: Geometry,
    val id: String,
    val properties: Properties,
    val type: String
)