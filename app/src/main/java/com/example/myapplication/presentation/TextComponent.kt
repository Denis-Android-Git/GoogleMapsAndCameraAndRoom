package com.example.myapplication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextComponent(
    modifier: Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .background(
                color = Color(0xFFFF9900),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun TextComponentPreview() {
    TextComponent(
        text = "Some Text",
        modifier = Modifier
    )
}