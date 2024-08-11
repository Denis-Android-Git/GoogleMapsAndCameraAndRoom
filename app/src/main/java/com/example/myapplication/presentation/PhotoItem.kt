package com.example.myapplication.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.Destinations
import com.example.myapplication.entity.Photo
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoItem(
    photo: Photo,
    navController: NavController,
    deleteList: MutableList<Photo>
) {
    val painter = rememberAsyncImagePainter(model = photo.uri)

    Box(
        modifier = Modifier
            .size(130.dp)
            .combinedClickable(
                onClick = {
                    val args = URLEncoder.encode(photo.uri, StandardCharsets.UTF_8.toString())
                    val date = photo.date
                    navController.navigate(Destinations.DetailScreen.withArgs(args, date))
                    deleteList.clear()
                },
                onLongClick = {
                    if (deleteList.contains(photo)) {
                        deleteList.remove(photo)
                    } else {
                        deleteList.add(photo)
                    }
                }
            )
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
        if (deleteList.contains(photo)) {
            Checkbox(
                checked = true, onCheckedChange = null
            )
        }
    }
}
