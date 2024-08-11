package com.example.myapplication.presentation

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.androidx.compose.koinViewModel

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = koinViewModel()
) {
    val info by mapViewModel.detailInfo.collectAsState()
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
    val context = LocalContext.current
    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationResult = fusedLocationProviderClient.lastLocation
    var deviceLocation by remember {
        mutableStateOf<LatLng?>(null)
    }
    var lastKnownLocation by remember {
        mutableStateOf<Location?>(null)
    }

    var showText by remember { mutableStateOf(false) }

    locationResult.addOnSuccessListener {
        if (it != null) {
            deviceLocation = LatLng(it.latitude, it.longitude)
            lastKnownLocation = it
            Log.d("lastKnownLocation", "lastKnownLocation========${it.latitude}")
        }
    }

    if (deviceLocation != null && lastKnownLocation != null) {

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(deviceLocation!!, 15f)
        }

        LaunchedEffect(key1 = Unit) {
            mapViewModel.getPlaces(lastKnownLocation!!.longitude, lastKnownLocation!!.latitude)
        }

        val places by mapViewModel.places.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GoogleMap(
                properties = properties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState
            ) {
                for (place in places) {

                    val position =
                        LatLng(place.geometry.coordinates[1], place.geometry.coordinates[0])

                    MarkerInfoWindowContent(
                        state = MarkerState(position = position),
                        onInfoWindowClick = {
                            mapViewModel.getInfo(place.properties.xid)
                            showText = true
                        },
                        onInfoWindowClose = {
                            showText = false
                        }
                    ) {
                        Column {
                            Text(it.title ?: place.properties.name, color = Color.Red)
                        }
                    }
                }
            }

            val km = lastKnownLocation!!.speed.times(3.6).toInt()

            Log.d("speed", "speed======${lastKnownLocation!!.speed}")

            Text(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                text = "$km km/h",
                fontSize = 15.sp,
            )
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
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
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