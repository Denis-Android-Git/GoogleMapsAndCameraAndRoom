package com.example.myapplication.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.myapplication.data.Destinations
import com.example.myapplication.viewmodel.DbViewModel
import com.example.myapplication.viewmodel.DetailScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DetailScreen(
    image: String,
    date: String?,
    navController: NavController,
    viewModel: DbViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    detailScreenViewModel: DetailScreenViewModel = koinViewModel()
) {
    var color by remember {
        mutableStateOf(Color.Transparent)
    }
    LaunchedEffect(Unit) {
        viewModel.getPhotoById(image)
        delay(1000)
        color = Color.Gray
    }
    detailScreenViewModel.image.value = image
    val showDelete = detailScreenViewModel.showDelete.value
    val photo by viewModel.photo.collectAsStateWithLifecycle()

    val showDeleteConfirmation by detailScreenViewModel.showDeleteConfirmation.collectAsStateWithLifecycle()
    val showImage by detailScreenViewModel.showImage.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        (if (!showImage) null else image)?.let {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, color, RoundedCornerShape(16.dp))
                    .align(Alignment.Center)
                    .sharedElement(
                        state = rememberSharedContentState(key = it),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        }
                    )
                    .fillMaxWidth(),
                model = it,
                contentDescription = null
            ) {
                when (val state = painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        state.result.throwable.message?.let { error ->
                            Text(text = error)
                        }
                    }

                    else -> {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
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
        AnimatedVisibility(
            showDelete,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp)
                .systemBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, shape = CircleShape)
                    .clickable { detailScreenViewModel.setShowDeleteConfirmationValue(true) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete all photos",
                    tint = Color.Black,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { detailScreenViewModel.setShowDeleteConfirmationValue(false) },
                title = { Text("Confirmation") },
                text = { Text("Are you sure?") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                val currentPhoto = photo
                                currentPhoto?.let {
                                    viewModel.deleteOnePhoto(photo = currentPhoto)
                                }

                                detailScreenViewModel.setShowDeleteConfirmationValue(false)
                                detailScreenViewModel.setShowImageValue(false)
                                navController.navigate(Destinations.MainScreen.routes)
                            }
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
                            detailScreenViewModel.setShowDeleteConfirmationValue(false)
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
