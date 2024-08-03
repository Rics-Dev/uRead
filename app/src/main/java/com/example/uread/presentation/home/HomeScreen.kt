// File: presentation/main/HomeScreen.kt

package com.example.uread.presentation.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.uread.data.source.local.SharedPreferencesUtil
//import androidx.navigation.NavHostController
import com.example.uread.presentation.book_shelf.ShelfPageScreen
import com.example.uread.presentation.home.components.AddBookSnackbar
import com.example.uread.presentation.home.components.BookCard
import com.example.uread.presentation.home.components.BookCardSkeleton
import com.example.uread.presentation.sharedComponents.Shelves
import com.example.uread.util.Navigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val books = viewModel.books.collectAsLazyPagingItems()
    val isLoadingNewBook by viewModel.isLoadingNewBooks.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val shelves = remember { mutableStateListOf("All Books") }
    val pagerState = rememberPagerState { shelves.size }


    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    val getContent =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                viewModel.handleDirectorySelection(it)
            }
        }

    LaunchedEffect(Unit) {
        viewModel.initializeApp(
            onFirstLaunch = { getContent.launch(null) }
        )
    }

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTab = pagerState.currentPage
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (selectedTab) {
                            0 -> "uRead"
                            else -> "Shelf $selectedTab"
                        }
                    )
                }
            )
        },
        snackbarHost = {
            AddBookSnackbar(
                snackbarHostState = snackbarHostState,
                isLoadingNewBook = isLoadingNewBook
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Shelves(shelves, selectedTab) { index ->
                selectedTab = index
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.background)
            ) { index ->
                when (index) {
                    0 -> {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(100.dp),
                                contentPadding = PaddingValues(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    count = books.itemCount,
                                    key = { index -> books[index]?.uri ?: index }
                                ) { index ->
                                    val book = books[index]
                                    BookCard(
                                        book,
                                        openBook = { openedBook ->
                                            openedBook.uri.let { uri ->
                                                navController.navigate(Navigation.BookReaderScreen.createRoute(uri.toString()))
                                            }

                                        }
                                    )
                                }
                            }
                        }

                    else -> ShelfPageScreen(index)
                }
            }
        }
    }
}

