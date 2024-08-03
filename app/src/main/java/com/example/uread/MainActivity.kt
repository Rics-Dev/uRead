package com.example.uread

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uread.presentation.bookReader.BookReaderScreen
import com.example.uread.presentation.home.HomeScreen
import com.example.uread.ui.theme.UReadTheme
import com.example.uread.util.ScreenNavigation
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UReadTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = ScreenNavigation.HomeScreen.route
                ) {
                    composable(
                        route = ScreenNavigation.HomeScreen.route
                    ) {
                        HomeScreen(navController = navController)
                    }
                    composable(
                        route = ScreenNavigation.BookReaderScreen.route + "/{bookUri}",
                    ) {
                        BookReaderScreen()
                    }
                }
            }
        }
    }
}







