package com.example.myapplication.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapScreen2(
    placeId: String,
    mapViewModel: MapViewModel = koinViewModel()
) {

    LaunchedEffect(Unit) {
        mapViewModel.getInfo(placeId)
    }

    val place by mapViewModel.detailInfo.collectAsStateWithLifecycle()

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false
            )
        )
    }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true
            )
        )
    }
    val cameraPositionState = rememberCameraPositionState()

    place?.let {
        cameraPositionState.position =
            CameraPosition.fromLatLngZoom(LatLng(it.point.lat, it.point.lon), 15f)
    }

    GoogleMap(
        contentPadding = PaddingValues(top = 20.dp),
        modifier = Modifier
            .fillMaxSize(),
        uiSettings = uiSettings,
        properties = properties,
        cameraPositionState = cameraPositionState
    ) {
        place?.let { placeInfo ->
            MarkerInfoWindowContent(
                state = rememberUpdatedMarkerState(
                    position = LatLng(
                        placeInfo.point.lat,
                        placeInfo.point.lon
                    )
                )
            ) {
                Text(
                    text = placeInfo.name,
                    color = Color.Red
                )
            }
        }
    }
}