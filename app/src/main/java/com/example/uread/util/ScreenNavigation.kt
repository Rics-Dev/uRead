package com.example.uread.util


sealed class ScreenNavigation(val route: String) {


    data object HomeScreen : ScreenNavigation("home_screen")
    data object BookReaderScreen: ScreenNavigation("book_reader_screen")

}