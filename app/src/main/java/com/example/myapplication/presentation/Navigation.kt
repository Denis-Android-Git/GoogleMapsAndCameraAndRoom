package com.example.myapplication.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.myapplication.data.Destinations
import com.example.myapplication.data.URI
import com.example.myapplication.viewmodel.MapViewModel
import com.example.myapplication.viewmodel.MyViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Navigation(
    viewModel: MyViewModel,
    navController: NavHostController,
    mapViewModel: MapViewModel = koinViewModel(),
) {
    val routeLink = viewModel.routeLink.collectAsState()

    LaunchedEffect(routeLink.value != null) {
        routeLink.value?.let { navController.navigate(it) }
    }

    NavHost(
        //modifier = modifier,
        navController = navController, startDestination = Destinations.MapScreen.routes
    ) {
        composable(
            route = Destinations.DetailScreen.routes + "/{name}/{date}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "No photo"
                    nullable = false
                },
                navArgument("date") {
                    type = NavType.StringType
                    defaultValue = "No date"
                    nullable = false
                }
            )
        ) { entry ->
            val image = entry.arguments?.getString("name")
            image?.let {
                DetailScreen(
                    image = it,
                    date = entry.arguments?.getString("date"),
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
        composable(
            route = Destinations.MapScreen.routes,
            deepLinks = listOf(navDeepLink {
                uriPattern = "$URI/${Destinations.MapScreen.routes}"
            })
        ) {
            MapScreen(
                mapViewModel = mapViewModel,
                navController = navController,
                myViewModel = viewModel
            )
        }
        composable(
            route = Destinations.XmlMap.routes
        ) {
            XmlMap()
        }
        composable(
            route = Destinations.LikedScreen.routes
        ) {
            PlacesNavigation(
                viewModel = viewModel
            )
        }
        composable(
            route = Destinations.SearchScreen.routes
        ) {
            SearchScreen()
        }
    }
}
