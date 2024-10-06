package com.example.myapplication.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.myapplication.data.Destinations
import com.example.myapplication.entity.db.Place
import com.example.myapplication.viewmodel.MapViewModel
import com.example.myapplication.viewmodel.MyViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    navController: NavController,
    myViewModel: MyViewModel
) {
    val info by mapViewModel.detailInfo.collectAsState()
    val places by mapViewModel.places.collectAsState()
    val speed by mapViewModel.speed.collectAsState()
    val error by mapViewModel.error.collectAsState()
    val location by mapViewModel.location.collectAsState()
    val scope = rememberCoroutineScope()
    val placeList by myViewModel.allPlaces.collectAsStateWithLifecycle()

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = true,
                zoomControlsEnabled = false
            )
        )
    }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true
            )
        )
    }

    var showText by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()

    location?.let {
        LaunchedEffect(key1 = location) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }
    location?.let {
        LaunchedEffect(key1 = location) {
            mapViewModel.getPlaces(it.longitude, it.latitude)
        }
    }
    var showButton by remember {
        mutableStateOf(false)
    }
    if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
        LaunchedEffect(key1 = Unit) {
            delay(500)
            showButton = true
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp)
    ) {
        GoogleMap(
            contentPadding = PaddingValues(top = 20.dp),
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState
        ) {
            places.map { place ->

                val position =
                    LatLng(place.geometry.coordinates[1], place.geometry.coordinates[0])

                MarkerInfoWindowContent(
                    state = rememberMarkerState(position = position),
                    onInfoWindowClick = {
                        mapViewModel.getInfo(place.properties.xid)
                        if (place.properties.name.isNotEmpty()) {
                            showText = true
                        }
                    },
                    onInfoWindowClose = {
                        showText = false
                    }
                ) {
                    Text(place.properties.name, color = Color.Red)
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .systemBarsPadding(),
            visible = showButton
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                ),
                onClick = {
                    scope.launch {
                        mapViewModel.clearPlaces()
                        delay(100)
                        mapViewModel.getPlaces(
                            cameraPositionState.position.target.longitude,
                            cameraPositionState.position.target.latitude
                        )
                        showButton = false
                    }
                }) {
                Text(text = "Искать здесь")
            }
        }

        TextComponent(
            text = if (speed == null) "0 km/h" else "$speed km/h",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .systemBarsPadding()
        )

        error?.let {
            TextComponent(
                text = it,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        if (showText) {
            info?.let {
                val placeInDb = placeList.find { place ->
                    place.id == it.xid
                }
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
                    modifier = Modifier
                        .animateContentSize()
                        .padding(1.dp)
                        .align(Alignment.Center)
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
                        AnimatedVisibility(isExpanded && it.wikipedia_extracts != null) {
                            //Log.d("Image", info!!.image)

                            Row {
                                SubcomposeAsyncImage(
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 16.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            val image = URLEncoder.encode(
                                                it.preview?.source,
                                                StandardCharsets.UTF_8.toString()
                                            )
                                            navController.navigate(
                                                Destinations.DetailScreen.withArgs(
                                                    image, ""
                                                )
                                            )
                                        }
                                        .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                                    model = it.preview?.source,
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
                                            if (placeInDb == null) {
                                                val place = Place(
                                                    id = it.xid,
                                                    title = it.name,
                                                    picture = it.preview?.source,
                                                    latitude = it.point.lat,
                                                    longitude = it.point.lon
                                                )
                                                myViewModel.addPlace(place)
                                            } else {
                                                myViewModel.deletePlace(placeInDb)
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
                                text = it.wikipedia_extracts?.text ?: "Нет информации",
                                fontSize = 15.sp,
                                color = if (isExpanded) Color.White else Color.Black,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .align(Alignment.CenterVertically),
                                onClick = {
                                    scope.launch {
                                        if (placeInDb == null) {
                                            val place = Place(
                                                id = it.xid,
                                                title = it.name,
                                                picture = it.preview?.source,
                                                latitude = it.point.lat,
                                                longitude = it.point.lon
                                            )
                                            myViewModel.addPlace(place)
                                        } else {
                                            myViewModel.deletePlace(placeInDb)
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
        }
    }
}