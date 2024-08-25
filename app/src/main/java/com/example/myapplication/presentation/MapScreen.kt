package com.example.myapplication.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.myapplication.viewmodel.MapViewModel
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
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = koinViewModel()
) {
    val info by mapViewModel.detailInfo.collectAsState()
    val places by mapViewModel.places.collectAsState()
    val speed by mapViewModel.speed.collectAsState()
    val error by mapViewModel.error.collectAsState()
    val location by mapViewModel.location.collectAsState()
    val scope = rememberCoroutineScope()

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

    if (location != null) {
        LaunchedEffect(key1 = Unit) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location!!, 15f)
        }
    }
    if (location != null) {
        LaunchedEffect(key1 = Unit) {
            mapViewModel.getPlaces(location!!.longitude, location!!.latitude)
        }
    }
    var showButton by remember {
        mutableStateOf(false)
    }
    if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
        LaunchedEffect(key1 = Unit) {
            delay(1000)
            showButton = true
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            contentPadding = PaddingValues(top = 20.dp),
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState
        ) {
            for (place in places) {

                val position =
                    LatLng(place.geometry.coordinates[1], place.geometry.coordinates[0])

                MarkerInfoWindowContent(
                    state = rememberMarkerState(position = position),
                    onInfoWindowClick = {
                        mapViewModel.getInfo(place.properties.xid)
                        showText = true
                    },
                    onInfoWindowClose = {
                        showText = false
                    }
                ) {
                    Column {
                        Text(place.properties.name, color = Color.Red)
                    }
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .systemBarsPadding(),
            visible = showButton
        ) {
            Button(onClick = {
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
        if (info != null && showText) {

            val wikipediaText = info?.wikipedia_extracts?.text ?: "Нет информации"
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
                        indication = rememberRipple(
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
                    AnimatedVisibility(isExpanded) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                            model = info?.preview?.source,
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
                        }
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        text = wikipediaText,
                        fontSize = 15.sp,
                        color = if (isExpanded) Color.White else Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}