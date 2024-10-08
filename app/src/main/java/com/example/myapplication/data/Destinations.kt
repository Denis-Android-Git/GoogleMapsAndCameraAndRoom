package com.example.myapplication.data

sealed class Destinations(val routes: String) {
    data object MainScreen : Destinations("main_screen")
    data object CameraScreen : Destinations("camera_screen")
    data object DetailScreen : Destinations("detail_screen")
    data object MapScreen : Destinations("map_screen")
    data object MapScreen2 : Destinations("map_screen_2")
    data object LikedScreen : Destinations("liked_screen")
    data object SearchScreen : Destinations("search_screen")
    data object XmlMap : Destinations("xml_map")

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(routes)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}