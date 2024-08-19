package com.example.myapplication.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
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
    SubcomposeAsyncImage(
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
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
        model = photo.uri,
        contentDescription = null
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
            CircularProgressIndicator(
                color = Color.White
            )
        } else {
            SubcomposeAsyncImageContent()
        }
        if (deleteList.contains(photo)) {
            Checkbox(
                checked = true, onCheckedChange = null
            )
        }
    }
}
