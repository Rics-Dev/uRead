package com.ricdev.uread.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ricdev.uread.presentation.annotations.AnnotationsScreen
import com.ricdev.uread.presentation.audioBookReader.AudiobookReaderScreen
import com.ricdev.uread.presentation.bookDetails.BookDetailsScreen
import com.ricdev.uread.presentation.bookReader.BookReaderScreen
import com.ricdev.uread.presentation.gettingStarted.GettingStartedScreen
import com.ricdev.uread.presentation.home.HomeScreen
import com.ricdev.uread.presentation.notes.NotesScreen
import com.ricdev.uread.presentation.pdfReader.PdfReaderScreen
import com.ricdev.uread.presentation.settings.SettingsScreen
import com.ricdev.uread.presentation.settings.components.AboutAppScreen
import com.ricdev.uread.presentation.settings.components.DeletedBooksScreen
import com.ricdev.uread.presentation.settings.components.GeneralSettings
import com.ricdev.uread.presentation.shelves.ShelvesScreen
import com.ricdev.uread.presentation.settings.components.ThemeScreen
import com.ricdev.uread.presentation.statistics.StatisticsScreen
import com.ricdev.uread.util.PurchaseHelper

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
    purchaseHelper: PurchaseHelper,
) {


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screens.GettingStartedScreen.route
        ) {
            GettingStartedScreen(navController)
        }
        composable(
            route = Screens.HomeScreen.route
        ) {
            HomeScreen(navController = navController, purchaseHelper)
        }
        composable(
            route = Screens.BookReaderScreen.route + "/{bookId}/{bookUri}",
        ) {
            BookReaderScreen(navController = navController, purchaseHelper)
        }
        composable(
            route = Screens.PdfReaderScreen.route + "/{bookId}/{bookUri}",
        ) {
            PdfReaderScreen(navController)
        }
        composable(
            route = Screens.AudiobookReaderScreen.route + "/{bookId}/{bookUri}",
        ) {
            AudiobookReaderScreen(navController)
        }
        composable(
            route = Screens.BookDetailsScreen.route + "/{bookId}/{bookUri}",
        ) {
            BookDetailsScreen(navController = navController)
        }
        composable(
            route = Screens.SettingsScreen.route,
        ) {
            SettingsScreen(navController, purchaseHelper)
        }
        composable(
            route = Screens.GeneralSettingsScreen.route,
        ) {
            GeneralSettings(navController)
        }
        composable(
            route = Screens.ThemeScreen.route,
        ) {
            ThemeScreen(navController, purchaseHelper)
        }
        composable(
            route = Screens.ShelvesScreen.route,
        ) {
            ShelvesScreen(navController, purchaseHelper)
        }
        composable(
            route = Screens.DeletedBooksScreen.route,
        ) {
            DeletedBooksScreen(navController)
        }
        composable(
            route = Screens.AboutAppScreen.route + "/{isDarkTheme}",
        ) {
            AboutAppScreen(navController)
        }

        composable(
            route = Screens.NotesScreen.route,
        ) {
            NotesScreen(navController, purchaseHelper)
        }
        composable(
            route = Screens.AnnotationsScreen.route,
        ) {
            AnnotationsScreen(navController, purchaseHelper)
        }
        composable(
            route = Screens.StatisticsScreen.route,
        ) {
            StatisticsScreen(navController, purchaseHelper)
        }
        //composable(
        //    route = Screens.OnlineBooksScreen.route,
        //) {
        //    OnlineBooksScreen(navController, purchaseHelper)
        //}
        //composable(
        //    route = "webview/{url}",
        //    arguments = listOf(navArgument("url") { type = NavType.StringType })
        //) { backStackEntry ->
        //    val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
        //    val decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
        //    WebViewScreen(navController = navController, url = decodedUrl)
        //}
    }


}

fun NavHostController.navigateToScreen(route: String) {
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(this@navigateToScreen.graph.startDestinationId) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reelecting the same item
        launchSingleTop = true
        // Restore state when reelecting a previously selected item
        restoreState = true
    }
}