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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.presentation.custom_progress_bars.TripleOrbitProgressBar
import com.example.myapplication.viewmodel.MapViewModel
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
    val info by mapViewModel.detailInfo.collectAsStateWithLifecycle()
    val places by mapViewModel.places.collectAsStateWithLifecycle()
    val speed by mapViewModel.speed.collectAsStateWithLifecycle()
    val error by mapViewModel.error.collectAsStateWithLifecycle()
    val cameraPosition by mapViewModel.cameraPosition.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val showButton by mapViewModel.showButton.collectAsStateWithLifecycle()
    val showText by mapViewModel.showText.collectAsStateWithLifecycle()
    val buttonText by mapViewModel.buttonText.collectAsStateWithLifecycle()
    val isFirstRun by mapViewModel.isFirstRun.collectAsStateWithLifecycle()

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

    val cameraPositionState = rememberCameraPositionState()

    DisposableEffect(true) {
        onDispose {
            mapViewModel.setShowTextValue(false)
        }
    }

    LaunchedEffect(key1 = cameraPosition) {
        cameraPosition?.let {
            cameraPositionState.position = it
            delay(500)
            if (isFirstRun) {
                mapViewModel.updateIsFirstRun(false)
            }
        }
    }

    if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
        LaunchedEffect(key1 = true) {
            delay(200)
            mapViewModel.setShowButtonValue(true)
        }
    }
    LaunchedEffect(key1 = cameraPositionState.isMoving) {
        if (!isFirstRun) {
            mapViewModel.updateCameraPosition(
                cameraPositionState.position
            )
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
                            mapViewModel.setShowTextValue(true)
                        }
                    },
                    onInfoWindowClose = {
                        mapViewModel.setShowTextValue(false)
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
                colors = ButtonDefaults.buttonColors(),
                onClick = {
                    scope.launch {
                        mapViewModel.clearPlaces()
                        delay(100)
                        mapViewModel.getPlaces(
                            cameraPositionState.position.target.longitude,
                            cameraPositionState.position.target.latitude
                        )
                        mapViewModel.setShowButtonValue(false)
                    }
                }) {
                Text(
                    text = stringResource(buttonText),
                    textAlign = TextAlign.Center
                )
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
                text = stringResource(it),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .systemBarsPadding()

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
        if (cameraPosition == null) {
            TripleOrbitProgressBar(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
            )
        }
    }
}