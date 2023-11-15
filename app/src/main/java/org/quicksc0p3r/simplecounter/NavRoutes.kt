package org.quicksc0p3r.simplecounter

sealed class NavRoutes(val route: String) {
    object Main: NavRoutes("main")
    object Settings: NavRoutes("settings")
    object About: NavRoutes("about")
    object FullscreenView: NavRoutes("fullscreen_view")

}