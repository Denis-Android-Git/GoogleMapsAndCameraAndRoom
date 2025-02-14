package com.example.myapplication.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.myapplication.data.Destinations
import com.example.myapplication.data.URI
import com.example.myapplication.viewmodel.DbViewModel
import com.example.myapplication.viewmodel.MapViewModel
import com.example.myapplication.viewmodel.IntentViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier,
    intentViewModel: IntentViewModel,
    navController: NavHostController,
    mapViewModel: MapViewModel = koinViewModel(),
    dbViewModel: DbViewModel = koinViewModel()
) {
    val routeLink by intentViewModel.routeLink.collectAsStateWithLifecycle()

    LaunchedEffect(routeLink) {
        routeLink?.let { navController.navigate(it) }
    }


    SharedTransitionLayout {
        NavHost(
            modifier = modifier,
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
                        viewModel = dbViewModel,
                        animatedVisibilityScope = this
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
                    animatedVisibilityScope = this
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
                    viewModel = dbViewModel
                )
            }
            composable(
                route = Destinations.SearchScreen.routes
            ) {
                SearchScreen(
                    navController = navController,
                    animatedVisibilityScope = this
                )
            }
        }
    }
}
