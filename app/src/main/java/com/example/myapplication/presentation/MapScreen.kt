package com.example.myapplication.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
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

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("MissingPermission")
@Composable
fun SharedTransitionScope.MapScreen(
    mapViewModel: MapViewModel,
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope
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

    LaunchedEffect(key1 = location) {
        location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
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
                Text(text = stringResource(R.string.search_here))
            }
        }

        TextComponent(
            text = if (speed == null) stringResource(R.string._0_km_h) else stringResource(
                R.string.km_h,
                speed ?: ""
            ),
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
                DetailInfoComponent(
                    modifier = Modifier.align(Alignment.Center),
                    detailInfoDto = it,
                    navController = navController,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}