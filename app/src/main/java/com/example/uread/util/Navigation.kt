package com.example.uread.util

import android.net.Uri

sealed class Navigation(val route: String) {


    data object HomeScreen : Navigation("home_screen")
    data object BookReaderScreen : Navigation("book_reader_screen/{bookUri}") {
        fun createRoute(bookUri: String) = "book_reader_screen/${Uri.encode(bookUri)}"
    }

}