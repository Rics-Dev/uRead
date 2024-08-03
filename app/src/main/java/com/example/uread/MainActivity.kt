package com.example.uread

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uread.presentation.bookReader.BookReaderScreen
import com.example.uread.presentation.home.HomeScreen
import com.example.uread.ui.theme.UReadTheme
import com.example.uread.util.Navigation
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
                    startDestination = Navigation.HomeScreen.route
                ) {
                    composable(
                        route = Navigation.HomeScreen.route
                    ) {
                        HomeScreen(navController = navController)
                    }
                    composable(
                        route = Navigation.BookReaderScreen.route,
                        arguments = listOf(navArgument("bookUri") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookUri = backStackEntry.arguments?.getString("bookUri") ?: return@composable
                        BookReaderScreen(bookUri = bookUri)
                    }
                }
            }
        }
    }
}







