package com.ricdev.uread.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Book
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.presentation.bookShelf.BookShelfScreen
import com.ricdev.uread.presentation.bookShelf.EmptyShelfContent
import com.ricdev.uread.presentation.home.components.AddBookSnackbar
import com.ricdev.uread.presentation.home.components.CustomBottomAppBar
import com.ricdev.uread.presentation.home.components.CustomSearchBar
import com.ricdev.uread.presentation.home.components.CustomTopAppBar
import com.ricdev.uread.presentation.home.components.GridLayout
import com.ricdev.uread.presentation.home.components.LayoutModal
import com.ricdev.uread.presentation.home.components.ListLayout
import com.ricdev.uread.presentation.home.components.SortFilterModal
import com.ricdev.uread.presentation.sharedComponents.CustomNavigationDrawer
import com.ricdev.uread.presentation.sharedComponents.Shelves
import com.ricdev.uread.util.PurchaseHelper
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    purchaseHelper: PurchaseHelper,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()


    val appPreferences by viewModel.appPreferences.collectAsStateWithLifecycle()
    val shelves by viewModel.shelves.collectAsStateWithLifecycle()
    val isAddingBooks by viewModel.isAddingBooks.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val booksInShelf by viewModel.booksInShelfSet.collectAsStateWithLifecycle()
    val books = viewModel.books.collectAsLazyPagingItems()

    var selectedTab by remember { mutableIntStateOf(0) }
    val selectedTabRow by viewModel.selectedTabRow.collectAsStateWithLifecycle()


    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


    val allShelves = remember(shelves) { listOf("All Books") + shelves.map { it.name } }
    val pagerState =
        rememberPagerState { allShelves.size }


    var selectedBooks by remember { mutableStateOf(listOf<Book>()) }
    var selectionMode by remember { mutableStateOf(false) }
    var searchMode by remember { mutableStateOf(false) }


    var showLayoutModal by remember { mutableStateOf(false) }
    var showSortModal by remember { mutableStateOf(false) }


    // Toggle book selection
    fun toggleSelection(book: Book) {
        selectedBooks = if (selectedBooks.contains(book)) {
            selectedBooks - book
        } else {
            selectedBooks + book
        }
        selectionMode = selectedBooks.isNotEmpty()
    }


    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
        if (selectedTab == 0) {
            viewModel.updateCurrentShelf(null)
        } else {
            val shelf = shelves.getOrNull(selectedTab - 1)
            viewModel.updateCurrentShelf(shelf)
            shelf?.let { viewModel.getBooksForShelf(it.id) }
        }

    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTab = pagerState.currentPage
            selectionMode = false
            selectedBooks = emptyList()
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                )
            }
        }
    }


    CustomNavigationDrawer(
        purchaseHelper = purchaseHelper,
        drawerState = drawerState,
        navController = navController
    ) {
        Scaffold(
            topBar = {
                AnimatedVisibility(
                    visible = searchMode,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                ) {
                    TopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        title = {

                            CustomSearchBar(
                                query = searchQuery,
                                onQueryChange = { viewModel.updateSearchQuery(it) },
                                onClose = {
                                    searchMode = false
                                    viewModel.updateSearchQuery("")
                                }
                            )
                        }
                    )
                }
                AnimatedVisibility(
                    visible = !searchMode,
                    enter = slideInHorizontally(initialOffsetX = { -it }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    CustomTopAppBar(
                        viewModel = viewModel,
                        selectedTab = selectedTab,
                        shelves = shelves,
                        selectedBooks = selectedBooks,
                        selectionMode = selectionMode,
                        clearSelection = {
                            selectedBooks = emptyList()
                            selectionMode = false
                        },
                        selectAll = {
                            selectedBooks = books.itemSnapshotList.items

                        },
                        appPreferences = appPreferences,

                        toggleLayoutModal = { showLayoutModal = true },
                        toggleSortFilterModal = { showSortModal = true },
                        totalBooks = books.itemCount,
                        currentShelfBookCount = booksInShelf.size,
                        toggleSearchMode = {
                            searchMode = true
                        },
                        openDrawer = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        },
                    )
                }
            },
            bottomBar = {
                CustomBottomAppBar(
                    selectionMode = selectionMode,
                    shelves = shelves,
                    selectedBooks = selectedBooks,
                    viewModel = viewModel,
                    clearSelection = {
                        selectedBooks = emptyList()
                        selectionMode = false
                    },
                    navController = navController
                )
            },
            snackbarHost = {
                AddBookSnackbar(
                    snackbarHostState = snackbarHostState,
                    isAddingBooks = isAddingBooks,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Shelves(
                    viewModel = viewModel,
                    appPreferences = appPreferences,
                    shelves = shelves,
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                    },
                    onAddShelf = { newShelfName ->
                        viewModel.addShelf(newShelfName)
                    },
                    purchaseHelper = purchaseHelper,
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(color = MaterialTheme.colorScheme.background)
                ) { index ->
                    when (index) {
                        0 -> {
                            var visible by remember { mutableStateOf(false) }

                            LaunchedEffect(Unit) {
                                visible =
                                    true  // Trigger animations when the composable is first displayed
                            }

                            val slideInAnimationSpec =
                                tween<IntOffset>(durationMillis = 300)
                            val tweenInAnimationSpec = tween<Float>(durationMillis = 300)

                            if (books.itemCount == 0 && appPreferences.readingStatus.isNotEmpty()) {
                                EmptyShelfContent("Library")
                            }
                            if (appPreferences.homeLayout == Layout.Grid || appPreferences.homeLayout == Layout.CoverOnly) {
                                AnimatedVisibility(
                                    visible = visible,
                                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                                        animationSpec = slideInAnimationSpec,
                                        initialOffsetY = { it })
                                ) {
                                    GridLayout(
                                        books = books,
                                        navController = navController,
                                        selectedBooks = selectedBooks,
                                        selectionMode = selectionMode,
                                        toggleSelection = {
                                            toggleSelection(it)
                                        },
                                        viewModel = viewModel,
                                        isLoading = isAddingBooks,
                                        appPreferences = appPreferences,
                                    )
                                }
                            } else {
                                AnimatedVisibility(
                                    visible = visible,
                                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                                        animationSpec = slideInAnimationSpec,
                                        initialOffsetY = { it })
                                ) {
                                    ListLayout(
                                        books = books,
                                        navController = navController,
                                        selectedBooks = selectedBooks,
                                        selectionMode = selectionMode,
                                        toggleSelection = {
                                            toggleSelection(it)
                                        },
                                        viewModel = viewModel,
                                        isLoading = isAddingBooks,
                                        appPreferences = appPreferences,
                                    )
                                }
                            }
                        }

                        else -> {
                            val shelf = shelves.getOrNull(index - 1)
                            if (shelf != null) {
                                BookShelfScreen(
                                    shelf = shelf,
                                    books = books,
                                    homeViewModel = viewModel,
                                    navController = navController,
                                    selectedBooks = selectedBooks,
                                    selectionMode = selectionMode,
                                    toggleSelection = ::toggleSelection,
                                    isLoading = isAddingBooks,
                                    appPreferences = appPreferences,
                                )
                            } else {
                                Text(stringResource(R.string.shelf_not_found))
                            }
                        }
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTabRow
                ) {
                    Tab(
                        selected = selectedTabRow == 0,
                        onClick = { viewModel.updateCurrentTabRow(0) },
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.MenuBook,
                                    contentDescription = "Ebooks"
                                )
                                Text("Ebooks")
                            }
                        }
                    )
                    Tab(
                        selected = selectedTabRow == 1,
                        onClick = { viewModel.updateCurrentTabRow(1) },
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Headset, contentDescription = "Ebooks")
                                Text("Audio Books")
                            }
                        }
                    )
                }
            }



            if (showLayoutModal) {
                LayoutModal(
                    appPreferences = appPreferences,
                    viewModel = viewModel,
                    onDismiss = { showLayoutModal = false },
                )
            }
            if (showSortModal) {
                SortFilterModal(
                    appPreferences = appPreferences,
                    viewModel = viewModel,
                    onDismiss = { showSortModal = false },
                )
            }

        }
    }
}