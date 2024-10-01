package com.example.myapplication.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.myapplication.R
import com.example.myapplication.entity.db.Place


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoItem(
    place: Place,
    navController: NavController,
    deleteList: MutableList<Place>
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .size(130.dp)
                .combinedClickable(
                    onClick = {
//                    val args = URLEncoder.encode(place.uri, StandardCharsets.UTF_8.toString())
//                    val date = place.date
//                    navController.navigate(Destinations.DetailScreen.withArgs(args, date))
//                    deleteList.clear()
                    },
                    onLongClick = {
                        if (deleteList.contains(place)) {
                            deleteList.remove(place)
                        } else {
                            deleteList.add(place)
                        }
                    }
                )
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
            model = place.picture ?: R.drawable.picture,
            contentScale = ContentScale.Crop,
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
            if (deleteList.contains(place)) {
                Checkbox(
                    checked = true, onCheckedChange = null
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = place.title,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = Color.Red
            )
        }
    }
}

@Preview
@Composable
fun PhotoItemPreview() {
    val navController = rememberNavController()
    val place = Place(
        id = "",
        title = "Собор большой огромный",
        latitude = 0.0,
        longitude = 0.0,
        picture = null
    )
    PhotoItem(
        deleteList = mutableListOf(),
        navController = navController,
        place = place
    )
}
