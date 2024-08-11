package com.example.myapplication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.Destinations
import com.example.myapplication.viewmodel.MyViewModel


@Composable
fun DetailScreen(
    name: String?,
    date: String?,
    navController: NavController,
    viewModel: MyViewModel
) {
    val photoList by viewModel.allPhotos.collectAsState()
    val painter = rememberAsyncImagePainter(model = name)
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        (if (!showImage) null else painter)?.let {
            Image(
                painter = it, contentDescription = null, modifier = Modifier.fillMaxSize()
            )
            Text(
                text = date ?: "",
                fontSize = 20.sp,
                color = Color.White,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .offset(y = (-60).dp)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(48.dp)
                .background(Color.White, shape = CircleShape)
                .clickable { showDeleteConfirmation = true }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete all photos",
                tint = Color.Black,
                modifier = Modifier.padding(12.dp)
            )
        }
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirmation") },
                text = { Text("Are you sure?") },
                confirmButton = {
                    Button(
                        onClick = {
                            photoList.lastOrNull()?.let {
                                viewModel.deleteOnePhoto(it)
                            }
                            showDeleteConfirmation = false
                            showImage = false
                            navController.navigate(Destinations.MainScreen.routes)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            Color.LightGray
                        )
                    ) {
                        Text(text = "Delete", color = Color.DarkGray)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmation = false
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            Color.LightGray
                        )
                    ) {
                        Text(text = "Cancel", color = Color.DarkGray)
                    }
                }
            )
        }
    }
}
