package com.ricdev.uread.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.ricdev.uread.R
import com.ricdev.uread.data.model.Layout
import com.ricdev.uread.presentation.bookShelf.BookShelfScreen
import com.ricdev.uread.presentation.home.components.CustomSnackbar
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
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val booksInShelf by viewModel.booksInShelfSet.collectAsStateWithLifecycle()
    val books = viewModel.books.collectAsLazyPagingItems()

    val importProgress by viewModel.importProgressState.collectAsStateWithLifecycle()
    val snackbarState by viewModel.snackbarState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val selectedTabRow by viewModel.selectedTabRow.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val allShelves = remember(shelves) { listOf("All Books") + shelves.map { it.name } }
    val pagerState = rememberPagerState { allShelves.size }

    var searchMode by remember { mutableStateOf(false) }
    val selectedBooks by viewModel.selectedBooks.collectAsStateWithLifecycle()
    val selectionMode by viewModel.selectionMode.collectAsStateWithLifecycle()

    var showLayoutModal by remember { mutableStateOf(false) }
    var showSortModal by remember { mutableStateOf(false) }

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
        viewModel.clearBookSelection()
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
                            viewModel.clearBookSelection()
                        },
                        selectAll = {
                            viewModel.selectAllBooks(books.itemSnapshotList.items)
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
                AnimatedVisibility(
                   visible = !selectionMode,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                ) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon( Icons.AutoMirrored.Rounded.MenuBook, contentDescription = "Ebooks") },
                            label = { Text("eBooks") },
                            selected = selectedTabRow == 0,
                            onClick = { viewModel.updateCurrentTabRow(0) }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Headset, contentDescription = "AudioBooks") },
                            label = { Text("AudioBooks") },
                            selected = selectedTabRow == 1,
                            onClick = { viewModel.updateCurrentTabRow(1) }
                        )
                    }
                }

               AnimatedVisibility(
                   visible = selectionMode,
                   enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                   exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
               ) {
                   CustomBottomAppBar(
                       shelves = shelves,
                       selectedBooks = selectedBooks,
                       viewModel = viewModel,
                       clearSelection = {
                           viewModel.clearBookSelection()
                       },
                       navController = navController
                   )
               }
            },
            snackbarHost = {
                CustomSnackbar(
                    snackbarState = snackbarState,
                    importProgressState = importProgress,
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
                                visible = true
                            }

                            val slideInAnimationSpec = tween<IntOffset>(durationMillis = 300)
                            val tweenInAnimationSpec = tween<Float>(durationMillis = 300)


//                            if (books.itemCount == 0) {
//                                EmptyShelfContent("Library")
//                            }

                            if (appPreferences.homeLayout == Layout.Grid || appPreferences.homeLayout == Layout.CoverOnly) {
                                AnimatedVisibility(
                                    visible = visible,
                                    enter = fadeIn(tweenInAnimationSpec) + slideInVertically(
                                        animationSpec = slideInAnimationSpec,
                                        initialOffsetY = { it })
                                ) {
                                    GridLayout(
                                        clearSearch = { viewModel.updateSearchQuery("") },
                                        books = books,
                                        navController = navController,
                                        selectedBooks = selectedBooks,
                                        selectionMode = selectionMode,
                                        toggleSelection = {
                                            viewModel.toggleBookSelection(it)
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
                                        clearSearch = { viewModel.updateSearchQuery("") },
                                        books = books,
                                        navController = navController,
                                        selectedBooks = selectedBooks,
                                        selectionMode = selectionMode,
                                        toggleSelection = {
                                            viewModel.toggleBookSelection(it)
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
                                    clearSearch = { viewModel.updateSearchQuery("") },
                                    shelf = shelf,
                                    books = books,
                                    homeViewModel = viewModel,
                                    navController = navController,
                                    selectedBooks = selectedBooks,
                                    selectionMode = selectionMode,
                                    toggleSelection = { book -> viewModel.toggleBookSelection(book) },
                                    isLoading = isAddingBooks,
                                    appPreferences = appPreferences,
                                )
                            } else {
                                Text(stringResource(R.string.shelf_not_found))
                            }
                        }
                    }
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