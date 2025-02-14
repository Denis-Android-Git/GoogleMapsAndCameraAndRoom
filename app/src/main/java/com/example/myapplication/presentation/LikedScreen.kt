package com.example.myapplication.presentation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.viewmodel.DbViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LikedScreen(
    navController: NavController,
    viewModel: DbViewModel
) {
    val deleteList by viewModel.deleteList.collectAsStateWithLifecycle()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val placeList by viewModel.allPlaces.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {

            if (placeList.isEmpty()) {
                TextContent()
            } else {
                LazyVerticalGrid (
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = WindowInsets.systemBars.asPaddingValues()//PaddingValues(horizontal = 40.dp)
                ) {
                    items(placeList,
                        key = {
                            it.id
                        }) { place ->
                        PhotoItem(
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = //tween(durationMillis = 650)
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ),
                            place = place,
                            navController = navController,
                            dbViewModel = viewModel,
//                            modifier = Modifier
                        )
                    }
                }
            }

            if (showDeleteConfirmation && deleteList.isNotEmpty()) {
                AlertDialog(onDismissRequest = {
                    showDeleteConfirmation = false
                    viewModel.changeDeleteList(emptyList())
                },
                    title = { Text("Confirmation") },
                    text = { Text("Delete all or selected?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                deleteList.forEach {
                                    viewModel.deletePlace(it)
                                }
                                showDeleteConfirmation = false
                                viewModel.changeDeleteList(emptyList())
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.LightGray
                            )
                        ) {
                            Text(text = "Delete selected", color = Color.DarkGray)
                        }
                        Button(
                            onClick = {
                                viewModel.deleteAllPlaces()
                                showDeleteConfirmation = false
                                viewModel.changeDeleteList(emptyList())
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                Color.LightGray
                            )
                        ) {
                            Text(text = "Delete all", color = Color.DarkGray)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDeleteConfirmation = false
                                viewModel.changeDeleteList(emptyList())
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
                    })
            }
        }
        if (deleteList.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .systemBarsPadding()
                    .size(48.dp)
                    .background(Color.Black, shape = CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = "Delete all photos",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                        .clickable { showDeleteConfirmation = true })
            }
        }
//        FloatingActionButton(
//            containerColor = Color.Black,
//            elevation = FloatingActionButtonDefaults.elevation(
//                defaultElevation = 8.dp,
//                pressedElevation = 4.dp
//            ),
//            onClick = {
//                navController.navigate(Destinations.CameraScreen.routes)
//                camera.startCamera()
//                deleteList.clear()
////                    FirebaseCrashlytics.getInstance().log("Additional info")
////                    try {
////                        throw Exception("My test")
////                    } catch (e: Exception) {
////                        FirebaseCrashlytics.getInstance().recordException(e)
////                    }
//            },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(6.dp)
//                .systemBarsPadding()
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
//                contentDescription = "Camera",
//                tint = Color.White
//            )
//                Text(
//                    text = "Add a picture", color = Color.White
//                )
    }
}
