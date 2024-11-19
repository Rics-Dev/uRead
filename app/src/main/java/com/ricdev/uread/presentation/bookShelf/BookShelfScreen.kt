package com.ricdev.uread.presentation.bookShelf


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportContacts
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import com.ricdev.uread.R
import com.ricdev.uread.data.model.AppPreferences
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.data.model.Shelf
import com.ricdev.uread.presentation.home.HomeViewModel
import com.ricdev.uread.presentation.home.components.GridLayout
import com.ricdev.uread.presentation.home.components.ListLayout

@Composable
fun BookShelfScreen(
    clearSearch: () -> Unit,
    shelf: Shelf,
    books: LazyPagingItems<Book>,
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    selectedBooks: List<Book>,
    selectionMode: Boolean,
    toggleSelection: (Book) -> Unit,
    isLoading: Boolean,
    appPreferences: AppPreferences,
) {


    when {
        books.itemCount == 0 -> {
            EmptyShelfContent(shelf.name)
        }

        appPreferences.homeLayout == Layout.Grid || appPreferences.homeLayout == Layout.CoverOnly -> {
            GridLayout(
                clearSearch =  { clearSearch() },
                books = books,
                navController = navController,
                selectedBooks = selectedBooks,
                selectionMode = selectionMode,
                toggleSelection = toggleSelection,
                viewModel = homeViewModel,
                isLoading = isLoading,
                appPreferences = appPreferences,

                )
        }

        else -> {
            ListLayout(
                clearSearch = { clearSearch() },
                books = books,
                navController = navController,
                selectedBooks = selectedBooks,
                selectionMode = selectionMode,
                toggleSelection = toggleSelection,
                viewModel = homeViewModel,
                isLoading = isLoading,
                appPreferences = appPreferences,
            )
        }
    }
}




@Composable
fun EmptyShelfContent(shelf: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ImportContacts,
                contentDescription = "No books in this shelf",
                modifier = Modifier.size(48.dp)
            )
            Text(stringResource(R.string.no_books_in, shelf))

        }
    }
}

