package com.example.myapplication.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Добавьте достопримечательности",
            modifier = Modifier.align(Alignment.Center).padding(16.dp),
            fontSize = 25.sp,
            fontStyle = FontStyle.Italic,
            color = Color.White
        )
    }
}