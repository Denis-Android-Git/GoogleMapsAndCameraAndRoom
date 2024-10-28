package com.example.myapplication.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.myapplication.R
import com.example.myapplication.data.Destinations
import com.example.myapplication.data.dto.DetailInfoDto
import com.example.myapplication.entity.db.Place
import com.example.myapplication.viewmodel.MyViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DetailInfoComponent(
    modifier: Modifier,
    myViewModel: MyViewModel = koinViewModel(),
    detailInfoDto: DetailInfoDto,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    LaunchedEffect(detailInfoDto) {
        myViewModel.findPlaceInDb(detailInfoDto.xid)
    }

    val scope = rememberCoroutineScope()
    val placeInDb by myViewModel.placeInDb.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    val surfaceColor by animateColorAsState(
        if (isExpanded)
            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        label = ""
    )
    Surface(
        modifier = modifier
            .animateContentSize()
            .padding(1.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    //radius = 250.dp,
                    color = Color.DarkGray
                )
            ) { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 5.dp,
        color = surfaceColor,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AnimatedVisibility(isExpanded && detailInfoDto.wikipedia_extracts != null) {
                //Log.d("Image", info!!.image)

                Row {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                val image = URLEncoder.encode(
                                    detailInfoDto.preview?.source,
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate(
                                    Destinations.DetailScreen.withArgs(
                                        image, ""
                                    )
                                )
                            }
                            .sharedElement(
                                state = rememberSharedContentState(key = "${detailInfoDto.preview?.source}"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 1000)
                                }
                            )
                            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                        model = detailInfoDto.preview?.source,
                        contentDescription = null
                    ) {
                        when (val state = painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                CircularProgressIndicator(
                                    color = Color.White
                                )
                            }

                            is AsyncImagePainter.State.Error -> {
                                state.result.throwable.message?.let { error -> Text(text = error) }
                            }

                            else -> {
                                SubcomposeAsyncImageContent()
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier
                            .padding(end = 16.dp, top = 16.dp)
                            .align(Alignment.CenterVertically),
                        onClick = {
                            scope.launch {
                                val currentPlace = placeInDb
                                if (currentPlace == null) {
                                    val place = Place(
                                        id = detailInfoDto.xid,
                                        title = detailInfoDto.name,
                                        picture = detailInfoDto.preview?.source,
                                        latitude = detailInfoDto.point.lat,
                                        longitude = detailInfoDto.point.lon
                                    )
                                    myViewModel.addPlace(place)
                                } else {
                                    myViewModel.deletePlace(currentPlace)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            tint = if (placeInDb == null) Color.White else Color.Red,
                            contentDescription = null
                        )
                    }
                }
            }
            Row {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(16.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    text = detailInfoDto.wikipedia_extracts?.text
                        ?: stringResource(R.string.no_info),
                    fontSize = 15.sp,
                    color = if (isExpanded) Color.White else Color.Unspecified,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically),
                    onClick = {
                        scope.launch {
                            val currentPlace = placeInDb
                            if (currentPlace == null) {
                                val place = Place(
                                    id = detailInfoDto.xid,
                                    title = detailInfoDto.name,
                                    picture = detailInfoDto.preview?.source,
                                    latitude = detailInfoDto.point.lat,
                                    longitude = detailInfoDto.point.lon
                                )
                                myViewModel.addPlace(place)
                            } else {
                                myViewModel.deletePlace(currentPlace)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (placeInDb == null) Icons.Outlined.Favorite else Icons.Filled.Favorite,
                        tint = if (placeInDb == null) Color.LightGray else Color.Red,
                        contentDescription = null
                    )
                }
            }
        }
    }
}