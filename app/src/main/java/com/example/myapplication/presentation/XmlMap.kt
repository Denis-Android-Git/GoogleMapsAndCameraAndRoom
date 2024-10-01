package com.example.myapplication.presentation

import androidx.compose.runtime.Composable
import androidx.fragment.compose.AndroidFragment
import com.example.myapplication.MapsFragment

@Composable
fun XmlMap() {
    AndroidFragment(
        MapsFragment::class.java
    )
}