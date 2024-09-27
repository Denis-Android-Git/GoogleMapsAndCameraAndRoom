package com.example.myapplication.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.compose.AndroidFragment
import com.example.myapplication.MapsFragment

@Composable
fun XmlMap() {
    AndroidFragment(
        MapsFragment::class.java
    )
}

@Preview
@Composable
fun XmlMapPreview() {
    XmlMap()
}