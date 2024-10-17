package com.example.myapplication.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.States
import com.example.myapplication.entity.Feature
import com.example.myapplication.viewmodel.SearchViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinViewModel()
) {
    val text by searchViewModel.searchText.collectAsStateWithLifecycle()

    val isSearching by searchViewModel.isSearching.collectAsStateWithLifecycle()

    val states by searchViewModel.states.collectAsStateWithLifecycle()

    val location by searchViewModel.location.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState()

    val leftBottomPoint by searchViewModel.leftBottomPoint.collectAsStateWithLifecycle()

    val rightTopPoint by searchViewModel.rightTopPoint.collectAsStateWithLifecycle()

    val polygonPoints by searchViewModel.polygonPoints.collectAsStateWithLifecycle()

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var foundPlaces by remember {
        mutableStateOf<List<Feature>?>(null)
    }

    var showErrorDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(location) {
        location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isSearching) 0.dp else 16.dp),
                shadowElevation = 10.dp,
                colors = SearchBarDefaults.colors(
                    //containerColor = MaterialTheme.colorScheme.primary
                ),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = {
                            searchViewModel.onQueryChange(it)
                            searchViewModel.search(it, leftBottomPoint, rightTopPoint)
                        },
                        onSearch = searchViewModel::onQueryChange,
                        expanded = isSearching,
                        onExpandedChange = { searchViewModel.onExpandedChange() },
                        placeholder = { Text("Поиск мест") },
                        leadingIcon = {
                            AnimatedVisibility(isSearching) {
                                IconButton(
                                    onClick = {
                                        searchViewModel.onExpandedChange()
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            AnimatedVisibility(isSearching && text.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchViewModel.onQueryChange("")
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        }
                    )
                },
                expanded = isSearching,
                onExpandedChange = { searchViewModel.onExpandedChange() }
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 100.dp)
                        .fillMaxSize()
                ) {
                    when (val currentState = states) {
                        is States.Error -> {
                            currentState.error?.let {
                                showErrorDialog = true
                                error = it
                            }
                        }

                        is States.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        is States.Success -> {
                            foundPlaces = currentState.list
                        }
                    }

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
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize(),
                        uiSettings = uiSettings,
                        properties = properties,
                        cameraPositionState = cameraPositionState,
                        onMapLongClick = { coordinates ->
                            if (leftBottomPoint == null) {
                                searchViewModel.setLeftBottomPoint(coordinates)
                            } else {
                                searchViewModel.setAllPointsOfPolygon(coordinates)
                            }
                        }
                    ) {
                        foundPlaces?.let {
                            it.map { place ->
                                MarkerInfoWindowContent(
                                    state = rememberMarkerState(
                                        position = LatLng(
                                            place.geometry.coordinates[1],
                                            place.geometry.coordinates[0]
                                        )
                                    )
                                ) {
                                    Text(text = place.properties.name, color = Color.Red)
                                }
                            }
                        }
                        leftBottomPoint?.let {
                            MarkerComposable(
                                state = MarkerState(position = it)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                        rightTopPoint?.let {
                            MarkerComposable(
                                state = MarkerState(position = it)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                        if (polygonPoints.isNotEmpty()) {
                            Polygon(
                                points = polygonPoints,
                                fillColor = Color.Transparent,
                                strokeColor = Color.Red
                            )
                        }
                    }

                    leftBottomPoint?.let {
                        IconButton(
                            onClick = {
                                searchViewModel.clearPolygonPoints()
                            },
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    ) {
        if (showErrorDialog) {
            Dialog(
                onDismissRequest = { showErrorDialog = false }
            ) {
                Column {
                    Text(
                        text = error ?: "",
                        color = Color.Red
                    )
                    Button(
                        onClick = { showErrorDialog = false }
                    ) {
                        Text(text = "Понятно")
                    }
                }
            }
        }
    }
}