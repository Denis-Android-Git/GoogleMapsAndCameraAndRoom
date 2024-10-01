package com.example.myapplication.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.Destinations
import com.example.myapplication.entity.BottomItem
import com.example.myapplication.viewmodel.MyViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BottomNaviScreen(
    viewModel: MyViewModel,
) {
    val navController = rememberNavController()
    val bottomItems = listOf(
        BottomItem(
            icon = Icons.Default.LocationOn,
            destination = Destinations.MapScreen.routes
        ),
        BottomItem(
            icon = Icons.Default.Favorite,
            destination = Destinations.LikedScreen.routes
        ),
        BottomItem(
            icon = Icons.Default.Search,
            destination = Destinations.SearchScreen.routes
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(100.dp),
                containerColor = Color.Black,
            ) {
                bottomItems.forEachIndexed { _, bottomItem ->
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == bottomItem.destination
                    } == true
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        ),
                        icon = {
                            Icon(
                                imageVector = bottomItem.icon, contentDescription = null
                            )
                        },
                        onClick = {
                            navController.navigate(bottomItem.destination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        selected = selected
                    )
                }
            }
        }
    ) {
        Navigation(
            navController = navController,
            viewModel = viewModel
        )
    }
}