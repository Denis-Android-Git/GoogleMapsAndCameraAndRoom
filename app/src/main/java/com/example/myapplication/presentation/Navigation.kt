package com.example.myapplication.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.myapplication.data.Camera
import com.example.myapplication.data.Destinations
import com.example.myapplication.data.URI
import com.example.myapplication.entity.Photo
import com.example.myapplication.viewmodel.MyViewModel


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    viewModel: MyViewModel,
    camera: Camera,
    deleteList: SnapshotStateList<Photo>,
    previewView: PreviewView
) {
    val navController = rememberNavController()
    val routeLink = viewModel.routeLink.collectAsState()

    LaunchedEffect (routeLink.value != null) {
        routeLink.value?.let { navController.navigate(it) }
    }

    NavHost(
        navController = navController, startDestination = Destinations.MainScreen.routes
    ) {
        composable(
            route = Destinations.MainScreen.routes,
            deepLinks = listOf(navDeepLink {
                uriPattern = "$URI/${Destinations.MainScreen.routes}"
            })
        ) {
            MainScreen(
                navController,
                camera = camera,
                viewModel = viewModel,
                deleteList = deleteList
            )
        }
        composable(
            route = Destinations.CameraScreen.routes,
        ) {
            CameraScreen(
                navController,
                camera = camera,
                viewModel = viewModel,
                previewView = previewView
            )
        }
        composable(
            route = Destinations.DetailScreen.routes + "/{name}/{date}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "No photo"
                    nullable = true
                },
                navArgument("date") {
                    type = NavType.StringType
                    defaultValue = "No date"
                    nullable = true
                }
            )
        ) { entry ->
            DetailScreen(
                name = entry.arguments?.getString("name"),
                date = entry.arguments?.getString("date"),
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            route = Destinations.MapScreen.routes,
            deepLinks = listOf(navDeepLink {
                uriPattern = "$URI/${Destinations.MapScreen.routes}"
            })
        ) {
            MapScreen()
        }
    }
}
