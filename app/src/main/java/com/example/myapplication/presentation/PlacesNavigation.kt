package com.example.myapplication.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.Destinations
import com.example.myapplication.viewmodel.MyViewModel

@Composable
fun PlacesNavigation(
    viewModel: MyViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = Destinations.LikedScreen.routes
    ) {
        composable(
            route = Destinations.LikedScreen.routes
        ) {
            LikedScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            route = Destinations.MapScreen2.routes + "/{placeId}",
            arguments = listOf(
                navArgument("placeId") {
                    type = NavType.StringType
                }
            )
        ) {
            val placeId = it.arguments?.getString("placeId")
            placeId?.let {
                MapScreen2(
                    placeId = placeId
                )
            }
        }
    }
}