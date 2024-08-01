package com.example.uread.util

sealed class Navigation(val route: String) {


    data object HomeScreen : Navigation("home_screen")
//    data object BookDetailScreen : Navigation()
//    data object BookShelfScreen : Navigation()

}