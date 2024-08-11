package com.example.myapplication.data

sealed class Destinations(val routes: String) {
    data object MainScreen : Destinations("main_screen")
    data object CameraScreen : Destinations("camera_screen")
    data object DetailScreen : Destinations("detail_screen")
    data object MapScreen : Destinations("map_screen")

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(routes)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}