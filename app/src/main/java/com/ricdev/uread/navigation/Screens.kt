package com.ricdev.uread.navigation


sealed class Screens(val route: String) {

    data object GettingStartedScreen : Screens("getting_started_screen")
    data object HomeScreen : Screens("home_screen")
    data object BookReaderScreen: Screens("book_reader_screen")
    data object PdfReaderScreen: Screens("pdf_reader_screen")
    data object AudiobookReaderScreen: Screens("audiobook_reader_screen")
    data object BookDetailsScreen: Screens("book_details_screen")
    data object SettingsScreen: Screens("settings_screen")
    data object GeneralSettingsScreen: Screens("general_settings")
    data object ThemeScreen: Screens("theme_screen")
    data object DeletedBooksScreen: Screens("deleted_books_screen")
    data object ShelvesScreen: Screens("shelves_screen")
    data object AboutAppScreen: Screens("about_app_screen")



    data object NotesScreen: Screens("notes_screen")
    data object AnnotationsScreen: Screens("annotations_screen")
    data object StatisticsScreen: Screens("statistics_screen")
//    data object OnlineBooksScreen: Screens("online_books_screen")


    data object PremiumScreen: Screens("premium_screen")

}